#include "local_storage.h"
#include "ticket_lock.h"
#include <stdlib.h>


/*
 * TODO: Define the global TLS array.
 */
tls_data_t g_tls[MAX_THREADS];
ticket_lock tls_lock;

/*
 * TODO: Implement init_storage to initialize g_tls.
 */
void init_storage(void) {
    // TODO: Set all thread_id fields to -1 and data pointers to NULL.
    for (int i = 0; i < MAX_THREADS; i++) {
        g_tls[i].thread_id = -1;
        g_tls[i].data = NULL;
    }
    ticketlock_init(&tls_lock);
}

/*
 * TODO: Implement tls_thread_alloc to allocate a TLS entry for the calling thread.
 */
void tls_thread_alloc(void) {
    // TODO: Use your synchronization mechanism to safely allocate an entry.
    int64_t tid = (int64_t)pthread_self();

    ticketlock_acquire(&tls_lock);

    // Check if already allocated
    for (int i = 0; i < MAX_THREADS; i++) {
        if (g_tls[i].thread_id == tid) {
            ticketlock_release(&tls_lock);
            return;
        }
    }

    // Find free slot
    for (int i = 0; i < MAX_THREADS; i++) {
        if (g_tls[i].thread_id == -1) {
            g_tls[i].thread_id = tid;
            g_tls[i].data = NULL;
            ticketlock_release(&tls_lock);
            return;
        }
    }

    // No free slot
    exit(1);
}

/*
 * TODO: Implement get_tls_data to retrieve the TLS data for the calling thread.
 */
void* get_tls_data(void) {
    // TODO: Search for the calling thread's entry and return its data.
    int64_t tid = (int64_t)pthread_self();

    ticketlock_acquire(&tls_lock);

    for (int i = 0; i < MAX_THREADS; i++) {
        if (g_tls[i].thread_id == tid) {
            void* data = g_tls[i].data;
            ticketlock_release(&tls_lock);
            return data;
        }
    }

    // Not found
    ticketlock_release(&tls_lock);
    exit(2);
    return NULL;
}

/*
 * TODO: Implement set_tls_data to update the TLS data for the calling thread.
 */
void set_tls_data(void* data) {
    // TODO: Search for the calling thread's entry and set its data.
    int64_t tid = (int64_t)pthread_self();

    ticketlock_acquire(&tls_lock);

    for (int i = 0; i < MAX_THREADS; i++) {
        if (g_tls[i].thread_id == tid) {
            g_tls[i].data = data;
            ticketlock_release(&tls_lock);
            return;
        }
    }

    // Not found
    ticketlock_release(&tls_lock);
    exit(2);
}

/*
 * TODO: Implement tls_thread_free to free the TLS entry for the calling thread.
 */
void tls_thread_free(void) {
    // TODO: Reset the thread_id and data in the corresponding TLS entry.
    int64_t tid = (int64_t)pthread_self();

    ticketlock_acquire(&tls_lock);

    for (int i = 0; i < MAX_THREADS; i++) {
        if (g_tls[i].thread_id == tid) {
            g_tls[i].thread_id = -1;
            g_tls[i].data = NULL;
            ticketlock_release(&tls_lock);
            return;
        }
    }

    ticketlock_release(&tls_lock);
}
