#include "tas_semaphore.h"

/*
 * TODO: Implement semaphore_init using a TAS spinlock.
 */
void semaphore_init(semaphore* sem, int initial_value) {
    atomic_flag_clear(&sem->lock);
    sem->count=initial_value;
}

/*
 * TODO: Implement semaphore_wait using the TAS spinlock mechanism.
 */
void semaphore_wait(semaphore* sem) {
    // TODO: Acquire the spinlock, decrement the semaphore value, then release the spinlock.
    while(atomic_flag_test_and_set(&sem->lock)){
        sched_yield();
    }
    while(sem->count<=0){
        atomic_flag_clear(&sem->lock);
        sched_yield();

        while(atomic_flag_test_and_set(&sem->lock)){
            sched_yield();
        }

    }
    sem->count--; //entering the critical section
    
    atomic_flag_clear(&sem->lock); //release the lock
}

/*
 * TODO: Implement semaphore_signal using the TAS spinlock mechanism.
 */
void semaphore_signal(semaphore* sem) {
    // TODO: Acquire the spinlock, increment the semaphore value, then release the spinlock.

    while(atomic_flag_test_and_set(&sem->lock)){
        sched_yield();
    }
    sem->count++;

    atomic_flag_clear(&sem->lock);

}
