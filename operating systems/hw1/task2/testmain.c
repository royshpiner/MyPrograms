#include <stdio.h>
#include <pthread.h>
#include <unistd.h>     // for sleep()
#include "tl_semaphore.h"

#define NUM_THREADS 4
#define NUM_ITERATIONS 5

semaphore sem;

void* worker(void* arg) {
    int id = *(int*)arg;

    for (int i = 0; i < NUM_ITERATIONS; i++) {
        semaphore_wait(&sem);
        // Critical section
        printf("Thread %d: in critical section (iteration %d)\n", id, i + 1);
        sleep(1);  // simulate work
        printf("Thread %d: leaving critical section\n", id);
        semaphore_signal(&sem);
        sleep(1);  // simulate non-critical work
    }

    return NULL;
}

int main() {
    pthread_t threads[NUM_THREADS];
    int ids[NUM_THREADS];

    // Initialize semaphore with 1 (only 1 thread allowed at a time)
    semaphore_init(&sem, 1);

    // Create threads
    for (int i = 0; i < NUM_THREADS; i++) {
        ids[i] = i;
        pthread_create(&threads[i], NULL, worker, &ids[i]);
    }

    // Wait for all threads to finish
    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_join(threads[i], NULL);
    }

    printf("All threads completed.\n");
    return 0;
}