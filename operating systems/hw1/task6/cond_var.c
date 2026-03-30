#include "cond_var.h"

/*
 * TODO: Implement condition_variable_init.
 */
void condition_variable_init(condition_variable* cv) {
    // TODO: Initialize internal fields.
    atomic_init(&cv->waiters,0);
    atomic_init(&cv->signals, 0);
    ticketlock_init(&cv->internal_lock);
}

/*
 * TODO: Implement condition_variable_wait.
 */
void condition_variable_wait(condition_variable* cv, ticket_lock* ext_lock) {
    // TODO: Increase waiter count, release ext_lock, wait until signaled, then reacquire ext_lock.
    atomic_fetch_add(&cv->waiters,1);
    ticketlock_release(ext_lock);
    int signaled = 0;
    while (!signaled) {
        ticketlock_acquire(&cv->internal_lock);
        if (atomic_load(&cv->signals) > 0) {
            atomic_fetch_sub(&cv->signals, 1);
            signaled = 1;
        }
        ticketlock_release(&cv->internal_lock);
        if (!signaled) {
            sched_yield();
        }
    }
    atomic_fetch_sub(&cv->waiters, 1);    // Mark as no longer waiting
    ticketlock_acquire(ext_lock);
    

}

/*
 * TODO: Implement condition_variable_signal.
 */
void condition_variable_signal(condition_variable* cv) {
    // TODO: Signal one waiting thread.
    if (atomic_load(&cv->waiters) > 0) {
        atomic_fetch_add(&cv->signals,1);
        sched_yield();
    }
}

/*
 * TODO: Implement condition_variable_broadcast.
 */
void condition_variable_broadcast(condition_variable* cv) {
    // TODO: Signal all waiting threads.
    if (atomic_load(&cv->waiters) > 0) {
        atomic_fetch_add(&cv->signals, atomic_load(&cv->waiters)); // signal to all waiters
        sched_yield(); // Give waiting threads time to observe signal
    }
}
