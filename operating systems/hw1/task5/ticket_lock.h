#ifndef TICKET_LOCK_H
#define TICKET_LOCK_H

#include <stdatomic.h>
#include <sched.h> 

/*
 * Ticket lock struct definition
 */
typedef struct {
    atomic_int ticket;
    atomic_int cur_ticket;
} ticket_lock;

/*
 * Initializes the ticket lock pointed to by 'lock'.
 */
void ticketlock_init(ticket_lock* lock);

/*
 * Acquires the ticket lock pointed to by 'lock'.
 * The calling thread waits until it acquires the lock.
 */
void ticketlock_acquire(ticket_lock* lock);

/*
 * Releases the ticket lock pointed to by 'lock'.
 */
void ticketlock_release(ticket_lock* lock);

#endif // TICKET_LOCK_H