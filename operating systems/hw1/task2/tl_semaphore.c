#include "tl_semaphore.h"
#include <sched.h> 
/*
 * TODO: Implement semaphore_init for the Ticket Lock semaphore.
 */
void semaphore_init(semaphore* sem, int initial_value) {
    // TODO: Define the structure and initialize the semaphore.
    atomic_init(&(sem->lock.ticket), 0);
    atomic_init(&(sem->lock.cur_ticket), 0);
    atomic_init(&(sem->count), initial_value);
}

/*
 * TODO: Implement semaphore_wait using the Ticket Lock mechanism.
 */
void semaphore_wait(semaphore* sem) {
    // TODO: Obtain a ticket and wait until it is your turn; then decrement the semaphore value.
    atomic_int my_ticket = atomic_fetch_add(&sem->lock.ticket, 1);
    while (atomic_load(&sem->lock.cur_ticket) != my_ticket) {
        sched_yield();
    }
    while (sem->count <= 0) { //If no available spots, release the lock by incrementing cur_ticket
        atomic_fetch_add(&sem->lock.cur_ticket, 1);  // release
        sched_yield();

        my_ticket = atomic_fetch_add(&sem->lock.ticket, 1);    //resign a ticket
        while (atomic_load(&sem->lock.cur_ticket) != my_ticket) {
            sched_yield();
        }
    }
    sem->count--; // enter critical section
    atomic_fetch_add(&sem->lock.cur_ticket, 1); // release lock

}

/*
 * TODO: Implement semaphore_signal using the Ticket Lock mechanism.
 */
void semaphore_signal(semaphore* sem) {
    // TODO: Increment the semaphore value and allow the next ticket holder to proceed.
    atomic_int my_ticket = atomic_fetch_add(&sem->lock.ticket, 1);     // Acquire the ticket lock
    while (atomic_load(&sem->lock.cur_ticket) != my_ticket) {
        sched_yield();
    }

    sem->count++; // release a resource
    atomic_fetch_add(&sem->lock.cur_ticket, 1); // release lock
}
