#include "uthreads.h"

#define MAX_SIGNALS_PER_SEC 1000000

typedef unsigned long address_t;
# define JB_SP 6
# define JB_PC 7
static char stacks[MAX_THREAD_NUM][STACK_SIZE];

static thread_t threads[MAX_THREAD_NUM];
static int total_quantums = 0;
static int current_tid = 0;
static int quantum_length = 0;
static struct sigaction sa = {0};
static struct itimerval timer = {0};

//for ready queue management
static int ready_queue[MAX_THREAD_NUM];
static int ready_head = 0, ready_tail = 0, ready_size = 0;

static void ready_enqueue(int tid) {
    for (int i = 0, idx = ready_head; i < ready_size; ++i, idx = (idx + 1) % MAX_THREAD_NUM)
        if (ready_queue[idx] == tid) return;
    ready_queue[ready_tail] = tid;
    ready_tail = (ready_tail + 1) % MAX_THREAD_NUM;
    ready_size++;
}

static int ready_dequeue() {
    if (ready_size == 0) return -1;
    int tid = ready_queue[ready_head];
    ready_head = (ready_head + 1) % MAX_THREAD_NUM;
    ready_size--;
    return tid;
}

static void ready_remove(int tid) {
    int cnt = ready_size, idx = ready_head;
    for (int i = 0; i < cnt; ++i, idx = (idx + 1) % MAX_THREAD_NUM) {
        if (ready_queue[idx] == tid) {
            for (int j = i; j < cnt - 1; ++j) {
                int from = (ready_head + j + 1) % MAX_THREAD_NUM;
                int to = (ready_head + j) % MAX_THREAD_NUM;
                ready_queue[to] = ready_queue[from];
            }
            ready_tail = (ready_tail - 1 + MAX_THREAD_NUM) % MAX_THREAD_NUM;
            ready_size--;
            break;
        }
    }
}

int uthread_init(int quantum_usecs) {
    if (quantum_usecs<=0) { return -1; }

    quantum_length = quantum_usecs;
    total_quantums = 1;

    for (int i=0 ; i<MAX_THREAD_NUM ; i++) {
        thread_t thr;
        thr.state = THREAD_UNUSED;
        thr.entry = NULL;
        thr.quantums = 0;
        thr.sleep_until = 0;
        thr.tid = i;
        threads[i] = thr;
    }
    
    sigset_t sigset;
    sigemptyset(&sigset);
    sigaddset(&sigset, SIGVTALRM);
    if (sigprocmask(SIG_BLOCK, &sigset, NULL) == -1) {
        fprintf(stderr, "system error: signal masking failed\n");
        exit(1);
    }

    threads[0].state = THREAD_RUNNING;
    threads[0].quantums = 1;
    sigsetjmp(threads[0].env, 1);
    current_tid = 0;

    sa.sa_handler = timer_handler;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;

    if (sigaction(SIGVTALRM, &sa, NULL) < 0) {
        perror("sigaction");
        exit(1);
    }

    timer.it_value.tv_sec = quantum_usecs / MAX_SIGNALS_PER_SEC;
    timer.it_value.tv_usec = quantum_usecs % MAX_SIGNALS_PER_SEC;
    timer.it_interval = timer.it_value;

    if (setitimer(ITIMER_VIRTUAL, &timer, NULL) < 0) {
        perror("setitimer");
    }

    if (sigprocmask(SIG_UNBLOCK, &sigset, NULL) == -1) {
        fprintf(stderr, "system error: signal unmasking failed\n");
        exit(1);
    }

    return 0;
}

int uthread_spawn(thread_entry_point entry_point) {
    if (entry_point == NULL) {
        return -1;
    }

    for (int i = 1; i < MAX_THREAD_NUM; ++i) {
        if (threads[i].state == THREAD_UNUSED) {
            setup_thread(i, stacks[i], entry_point);
            ready_enqueue(i);
            return i;
        }
    }

    return -1;
}

int uthread_terminate(int tid) {
    if (tid < 0 || tid >= MAX_THREAD_NUM) {
        return -1;
    }

    thread_t *t = &threads[tid];

    if (t->state == THREAD_UNUSED) {
        return -1;
    }

    // If main thread, exit everything
    if (tid == 0) {
        // Free all other threads first
        for (int i = 1; i < MAX_THREAD_NUM; ++i) {
            threads[i].state = THREAD_UNUSED;
            threads[i].sleep_until = 0;
            threads[i].quantums = 0;
            threads[i].entry = NULL;
        }
        exit(0);
    }

    // Clean up this thread's control block
    t->state = THREAD_UNUSED;
    t->sleep_until = 0;
    t->quantums = 0;
    t->entry = NULL;
    ready_remove(tid);

    // If the thread is self-terminating (i.e. running itself)
    if (tid == current_tid) {
        schedule_next();  // This call never returns
    }

    return 0;
}

int uthread_block(int tid) {
    if (tid <= 0 || tid >= MAX_THREAD_NUM) {
        return -1;
    }
    thread_t *t = &threads[tid];

    if (t->state == THREAD_UNUSED) {
        return -1;
    }
    if (t->state == THREAD_BLOCKED) {
        return 0; // no-op
    }

    t->state = THREAD_BLOCKED;
    ready_remove(tid);

    if (tid == current_tid) {
        schedule_next();  // never returns
    }

    return 0;
}

int uthread_resume(int tid) {
    if (tid < 0 || tid >= MAX_THREAD_NUM) {
        return -1;
    }

    thread_t *t = &threads[tid];

    if (t->state == THREAD_UNUSED) {
        return -1;
    }

    if (t->state == THREAD_BLOCKED && t->sleep_until == 0) {
        t->state = THREAD_READY;
        ready_enqueue(tid);
    }

    return 0;
}

int uthread_sleep(int num_quantums) {
    if (current_tid == 0 || num_quantums <= 0) {
        return -1; // main thread may not sleep; or invalid input
    }

    thread_t *t = &threads[current_tid];

    t->sleep_until = total_quantums + num_quantums;
    t->state = THREAD_BLOCKED;
    ready_remove(current_tid);

    schedule_next(); // never returns

    return 0; // unreachable
}

int uthread_get_tid() {
    return current_tid;
}

int uthread_get_total_quantums() {
    return total_quantums;
}

int uthread_get_quantums(int tid) {
    if (tid < 0 || tid >= MAX_THREAD_NUM) {
        return -1;
    }

    thread_t *t = &threads[tid];

    if (t->state == THREAD_UNUSED) {
        return -1;
    }

    return t->quantums;
}


void timer_handler(int signum) {
    (void)signum;  // avoid unused variable warning

    // Wake up threads whose sleep has expired
    for (int i = 0; i < MAX_THREAD_NUM; ++i) {
        if (threads[i].state == THREAD_BLOCKED && threads[i].sleep_until > 0) {
            if (threads[i].sleep_until <= total_quantums) {
                threads[i].sleep_until = 0;
                threads[i].state = THREAD_READY;
                ready_enqueue(i);
            }
        }
    }

    // Schedule the next READY thread
    schedule_next();
}

void schedule_next() {
    int prev_tid = current_tid;
    total_quantums++;
    threads[current_tid].quantums++;

    // Mark the current thread as READY (unless it's terminated or blocked)
    if (threads[prev_tid].state == THREAD_RUNNING) {
        threads[prev_tid].state = THREAD_READY;
        ready_enqueue(prev_tid);
    }

    int next_tid = ready_dequeue();
    if (next_tid == -1) {
        fprintf(stderr, "No READY thread to schedule!\n");
        if (threads[prev_tid].state == THREAD_READY) {
            threads[prev_tid].state = THREAD_RUNNING;
        }
        return;
    }

    threads[next_tid].state = THREAD_RUNNING;
    current_tid = next_tid;

    context_switch(&threads[prev_tid], &threads[next_tid]);
}


void context_switch(thread_t *current, thread_t *next) {
    if (sigsetjmp(current->env, 1) == 0) {
        siglongjmp(next->env, 1);
    }
}

address_t translate_address(address_t addr) {
    address_t ret;
    asm volatile("xor    %%fs:0x30,%0\n"
                 "rol    $0x11,%0\n"
                 : "=g"(ret)
                 : "0"(addr));
    return ret;
}
// address_t translate_address(address_t addr) {
//     return addr; // No-op for ARM64 (Apple Silicon)
// }

void setup_thread(int tid, char *stack, thread_entry_point entry_point) {
    sigsetjmp(threads[tid].env, 1);     // Save initial context into the jump buffer
    address_t sp = (address_t)(stack + STACK_SIZE - sizeof(address_t)); // Calculate stack top
    address_t pc = (address_t)entry_point;
    // Architecture-specific translation
    threads[tid].env->__jmpbuf[JB_SP] = translate_address(sp);
    threads[tid].env->__jmpbuf[JB_PC] = translate_address(pc);
    // Save entry point
    threads[tid].entry = entry_point;
    // Mark thread as READY
    threads[tid].state = THREAD_READY;
}