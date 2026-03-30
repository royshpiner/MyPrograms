#include "rw_lock.h"

/*
 * TODO: Implement rwlock_init.
 */
void rwlock_init(rwlock* lock) {
    // TODO: Initialize the lock structure.
    ticketlock_init(&lock->lock);
    condition_variable_init(&lock->readers_cv);
    condition_variable_init(&lock->writers_cv);
    atomic_init(&lock->readers, 0);
    atomic_init(&lock->writers, 0);
    atomic_init(&lock->waiting_writers, 0);
}

/*
 * TODO: Implement rwlock_acquire_read.
 */
void rwlock_acquire_read(rwlock* lock) {
    // TODO: Allow multiple readers while ensuring no writer is active.
    ticketlock_acquire(&lock->lock);

    // Writer preference: wait if a writer is active or waiting
    while (atomic_load(&lock->writers) > 0 || atomic_load(&lock->waiting_writers) > 0) {
        condition_variable_wait(&lock->readers_cv, &lock->lock);
    }

    atomic_fetch_add(&lock->readers, 1);

    ticketlock_release(&lock->lock);

}

/*
 * TODO: Implement rwlock_release_read.
 */
void rwlock_release_read(rwlock* lock) {
    // TODO: Decrement the reader count.
    ticketlock_acquire(&lock->lock);

    if (atomic_fetch_sub(&lock->readers, 1) == 1) {
        // Last reader: wake up one writer
        condition_variable_signal(&lock->writers_cv);
    }

    ticketlock_release(&lock->lock);
}

/*
 * TODO: Implement rwlock_acquire_write.
 */
void rwlock_acquire_write(rwlock* lock) {
    // TODO: Ensure exclusive access for writing.
    ticketlock_acquire(&lock->lock);

    atomic_fetch_add(&lock->waiting_writers, 1);

    // Wait until no readers and no writer
    while (atomic_load(&lock->readers) > 0 || atomic_load(&lock->writers) > 0) {
        condition_variable_wait(&lock->writers_cv, &lock->lock);
    }

    atomic_fetch_sub(&lock->waiting_writers, 1);
    atomic_store(&lock->writers, 1); // Writer owns the lock

    ticketlock_release(&lock->lock);
}

/*
 * TODO: Implement rwlock_release_write.
 */
void rwlock_release_write(rwlock* lock) {
    // TODO: Release the write lock.
    ticketlock_acquire(&lock->lock);

    atomic_store(&lock->writers, 0);

    if (atomic_load(&lock->waiting_writers) > 0) {
        // Wake one writer
        condition_variable_signal(&lock->writers_cv);
    } else {
        // No writers waiting: wake all readers
        condition_variable_broadcast(&lock->readers_cv);
    }

    ticketlock_release(&lock->lock);
}
