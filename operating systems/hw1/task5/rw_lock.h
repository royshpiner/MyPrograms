#ifndef RW_LOCK_H
#define RW_LOCK_H

#include <stdatomic.h>
#include "cond_var.h"

/*
 * Define the read-write lock type.
 * Write your struct details in this file..
 */
typedef struct {
    // write your implementation here
    ticket_lock lock;               // Lock to protect internal state
    condition_variable readers_cv;  // Condition variable for readers
    condition_variable writers_cv;  // Condition variable for writers

    atomic_int readers;             // Current number of active readers
    atomic_int writers;             // 1 if a writer holds the lock
    atomic_int waiting_writers;     // Number of writers waiting

} rwlock;

/*
 * Initializes the read-write lock.
 */
void rwlock_init(rwlock* lock);

/*
 * Acquires the lock for reading.
 */
void rwlock_acquire_read(rwlock* lock);

/*
 * Releases the lock after reading.
 */
void rwlock_release_read(rwlock* lock);

/*
 * Acquires the lock for writing. This operation should ensure exclusive access.
 */
void rwlock_acquire_write(rwlock* lock);

/*
 * Releases the lock after writing.
 */
void rwlock_release_write(rwlock* lock);

#endif // RW_LOCK_H
