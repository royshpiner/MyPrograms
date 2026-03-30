#include "cp_pattern.h"
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "queue.h"
#include "cond_var.h"
#define MAX_SIZE 1000000

pthread_t* producer_threads;
pthread_t* consumer_threads;
ticket_lock queue_lock; // ticket lock to sync access to shared resources
Queue queue; //for storing produced numbers
int queue_size = 0; // for saving the size of the queue
int producers_amount = 0; //save the numbers of producers 
int consumers_amount = 0; // for saving the number of consumers
int is_generated[MAX_SIZE] = {0}; // Array to track generated numbers
int generated_counter = 0;// for tracking how many unique numbers have been generated
condition_variable is_empty; // Condition variable for signaling when the queue is empty
ticket_lock output_lock; // ticket loce to sync output

void* producersFunc(void* arg) {
    int rand_num = 0;
    char msg[128];
    while (1) {
        ticketlock_acquire(&queue_lock);
        if (generated_counter >= MAX_SIZE) {
            condition_variable_signal(&is_empty); // Wake up consumers
            ticketlock_release(&queue_lock);
            break; // FIX: Stop producing when all unique numbers have been generated
        }

        rand_num = rand() % MAX_SIZE; //generates a number not larger then max size
        if (!is_generated[rand_num]) { //if it wasn't generated already
            is_generated[rand_num] = 1;
            generated_counter++; // Track total unique numbers
            snprintf(msg, sizeof(msg), "Producer %lu generated number: %d\n", (unsigned long)pthread_self(), rand_num);  
            print_msg(msg);
            enqueue(&queue, rand_num); //producer adds the number generated to the queue
            queue_size++;
            condition_variable_signal(&is_empty); // Wake up waiting consumers
        }
        ticketlock_release(&queue_lock);
    }
    return NULL;
}

void* consumersFunc(void* arg) {
    int num = 0;
    char msg[128];
    while (1) {
        ticketlock_acquire(&queue_lock);
        while (queue_size == 0 && generated_counter < MAX_SIZE) { // wait until there are numbers in the queue or all number generated
            condition_variable_wait(&is_empty, &queue_lock);
        }

        if (queue_size == 0 && generated_counter >= MAX_SIZE) { // if all numbers were generated and the queue is empty
            ticketlock_release(&queue_lock);
            break; // Exit only if no more work is coming
        }
        num = dequeue(&queue); //number from the queue
        queue_size--;  //since we extract a number we decrease the size of the queue
        ticketlock_release(&queue_lock);
        snprintf(msg, sizeof(msg), "Consumer %lu checked %d. Is it divisible by 6? %s\n",(unsigned long)pthread_self(), num, (num % 6 == 0 ? "True" : "False"));
        print_msg(msg);
    }
    return NULL;
}
/*
 * TODO: Implement start_consumers_producers.
 * This function should:
 *  - Print the configuration (number of consumers, producers, seed).
 *  - Seed the random number generator using srand().
 *  - Create producer and consumer threads.
 */
void start_consumers_producers(int consumers, int producers, int seed) {
    printf("Number of Consumers: %d\nNumber of Producers: %d\nSeed: %d\n", consumers, producers, seed);
    srand(seed);

    for (int i = 0; i < producers; i++) {
        pthread_create(&producer_threads[i], NULL, producersFunc, NULL); //create threads for all producers by argument
    }
    for (int i = 0; i < consumers; i++) {
        pthread_create(&consumer_threads[i], NULL, consumersFunc, NULL); //create threads for all consumers by argument
    }
}

/*
 * TODO: Implement stop_consumers to stop all consumers threads.
 */
void stop_consumers() {
    for (int i = 0; i < consumers_amount; i++) {
        pthread_join(consumer_threads[i], NULL);
    }
}
/*
 * TODO: Implement print_msg to perform synchronized printing.
 */
void print_msg(const char* msg) {
    ticketlock_acquire(&output_lock); //acquire the ticketlock for printing the output
    printf("%s", msg);
    ticketlock_release(&output_lock);  //done printing, release the lock
}

/*
 * TODO: Implement wait_until_producers_produced_all_numbers 
 * The function should wait until all numbers between 0 and 1,000,000 have been produced.
 */
void wait_until_producers_produced_all_numbers() {
    ticketlock_acquire(&queue_lock);
    while (generated_counter < MAX_SIZE) { //Wait until all unique numbers have been generated
        condition_variable_wait(&is_empty, &queue_lock);
    }
    ticketlock_release(&queue_lock);
}


/*
 * TODO: Implement wait_consumers_queue_empty to wait until queue is empty, 
 * if queue is already empty - return immediately without waiting.
 */
void wait_consumers_queue_empty() {
    ticketlock_acquire(&queue_lock);
    while (queue_size > 0) {
        condition_variable_wait(&is_empty, &queue_lock);
    }
    ticketlock_release(&queue_lock);
}

/*
 * TODO: Implement a main function that controls the producer-consumer process
 */
int main(int argc, char* argv[]) {
    // TODO: Parse arguments.
    // TODO: Start producer-consumer process.
    // TODO: Wait for threads to finish and clean up resources.
    ticketlock_init(&queue_lock);
    ticketlock_init(&output_lock);
    initializeQueue(&queue);
    condition_variable_init(&is_empty);
    if (argc != 4) { //check if the amount of arguments isn't valid
        fprintf(stderr, "usage: cp_pattern [consumers] [producers] [seed]\n");
        return 1;
    }
    consumers_amount = atoi(argv[1]);  //amount of producers as the first argument
    producers_amount = atoi(argv[2]);   //amount of consumers as the second argument
    int seed = atoi(argv[3]);    // the seed amount

    if (consumers_amount <= 0 || producers_amount <= 0 || seed < 0) { //ensure there is at least 1 producer and 1 consumer
        fprintf(stderr, "usage: cp_pattern [consumers] [producers] [seed]\n");
        return 1;
    }
    producer_threads = malloc(producers_amount * sizeof(pthread_t));  
    consumer_threads = malloc(consumers_amount * sizeof(pthread_t));

    start_consumers_producers(consumers_amount, producers_amount, seed); //if all arguments are fine, start runnung
    wait_until_producers_produced_all_numbers(); 
    wait_consumers_queue_empty();
    stop_consumers(); //when done genrating all the numbers and the queue is empty, stop the consumers

    free(producer_threads); 
    free(consumer_threads);
    return 0;
}