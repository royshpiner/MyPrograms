

#include "monitor.h"

int monitor_init(monitor_t* m) {
    if (pthread_mutex_init(&m->mutex, NULL) != 0) return -1;
    if (pthread_cond_init(&m->condition, NULL) != 0) {
        (void)pthread_mutex_destroy(&m->mutex);
        return -1;
    }
    m->signaled = 0;
    return 0;
}

void monitor_destroy(monitor_t* m) { // destroy cond then mutex, call only when no waiters are blocked
    (void)pthread_cond_destroy(&m->condition);
    (void)pthread_mutex_destroy(&m->mutex);
}

void monitor_signal(monitor_t* m) { //  wake all current waiters
    pthread_mutex_lock(&m->mutex);
    m->signaled = 1;   // stays set until monitor_reset()
    pthread_cond_broadcast(&m->condition); // wake everyone
    pthread_mutex_unlock(&m->mutex);
}

void monitor_reset(monitor_t* m) { 
    pthread_mutex_lock(&m->mutex);
    m->signaled = 0;
    pthread_mutex_unlock(&m->mutex);
}

int monitor_wait(monitor_t* m) {
    pthread_mutex_lock(&m->mutex);
    while (!m->signaled) {  // handles spurious wakeups
        int rc = pthread_cond_wait(&m->condition, &m->mutex);
        if (rc != 0) { pthread_mutex_unlock(&m->mutex); return -1; }
    }
    pthread_mutex_unlock(&m->mutex);
    return 0;
}