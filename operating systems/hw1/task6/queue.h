#ifndef QUEUE_H
#define QUEUE_H

// Node definition
typedef struct Node {
    int data;
    struct Node* next;
} Node;

// Queue definition
typedef struct Queue {
    Node* front;
    Node* rear;
} Queue;

// Initializes the queue
void initializeQueue(Queue* q);

// Adds an element to the queue
void enqueue(Queue* q, int data);

// Removes and returns the front element from the queue
int dequeue(Queue* q);

#endif // QUEUE_H