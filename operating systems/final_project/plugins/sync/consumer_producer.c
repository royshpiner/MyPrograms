
#include <stdlib.h>
#include <string.h>
#include "consumer_producer.h"


//check memory leaks with asan after finishing project
const char* consumer_producer_init(consumer_producer_t* q, int capacity) {
    if (capacity <= 0) return "queue capacity must be positive";
    q->items = (char**)calloc((size_t)capacity, sizeof(char*));
    if (!q->items) return "queue allocation failed";
    q->capacity = capacity;
    q->count = 0;
    q->head = q->tail = 0;
    q->is_finished = 0;
    if (pthread_mutex_init(&q->lock, NULL) != 0) return "mutex init failed";
    if (monitor_init(&q->not_full) != 0) return "monitor init failed";  // signaled, space available
    if (monitor_init(&q->not_empty) != 0) return "monitor init failed"; // signaled, items available
    if (monitor_init(&q->finished) != 0) return "monitor init failed";  // signaled, shutdown requested
    // initially not_empty is reset; not_full is signaled (space available)
    monitor_signal(&q->not_full);
    return NULL;
}

void consumer_producer_destroy(consumer_producer_t* q) {
    // free any remaining items defensively
    if (q->items) {
        for (int i = 0; i < q->capacity; ++i) {
            if (q->items[i]) free(q->items[i]);
        }
    }
    free(q->items); q->items = NULL;
    monitor_destroy(&q->not_full);
    monitor_destroy(&q->not_empty);
    monitor_destroy(&q->finished);
    (void)pthread_mutex_destroy(&q->lock);
}

const char* consumer_producer_put(consumer_producer_t* q, const char* item) {
    // Copy input string for queue ownership
    char* copy = strdup(item);
    if (!copy) return "allocation failed";
    pthread_mutex_lock(&q->lock);
    // Wait while full
    while (q->count == q->capacity) {
        // reset not_full before waiting so signals are not lost
        monitor_reset(&q->not_full);
        pthread_mutex_unlock(&q->lock);
        if (monitor_wait(&q->not_full) != 0) { free(copy); return "wait not_full failed"; }
        pthread_mutex_lock(&q->lock);
    }
    q->items[q->tail] = copy;
    q->tail = (q->tail + 1) % q->capacity;
    q->count++;
    // Signal not_empty if we transitioned from empty
    monitor_signal(&q->not_empty);
    pthread_mutex_unlock(&q->lock);
    return NULL;
}

char* consumer_producer_get(consumer_producer_t* q) {
    pthread_mutex_lock(&q->lock);
    while (q->count == 0) {
        // If finished and empty: nothing more to consume
        if (q->is_finished) { pthread_mutex_unlock(&q->lock); return NULL; }
        monitor_reset(&q->not_empty);
        pthread_mutex_unlock(&q->lock);
        if (monitor_wait(&q->not_empty) != 0) { return NULL; }
        pthread_mutex_lock(&q->lock);
    }
    char* item = q->items[q->head];
    q->items[q->head] = NULL;
    q->head = (q->head + 1) % q->capacity;
    q->count--;
    // Signal not_full if we transitioned from full
    monitor_signal(&q->not_full);
    pthread_mutex_unlock(&q->lock);
    return item;
}

void consumer_producer_signal_finished(consumer_producer_t* q) {
    pthread_mutex_lock(&q->lock);
    q->is_finished = 1;
    pthread_mutex_unlock(&q->lock);
    monitor_signal(&q->finished);
    // Wake potential consumers/producers
    monitor_signal(&q->not_empty);
    monitor_signal(&q->not_full);
}

int consumer_producer_wait_finished(consumer_producer_t* q) {
    return monitor_wait(&q->finished);
}
