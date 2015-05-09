#SQS Retry Queue

Amazon SQS can be utilized to guarantee delivery and processing of messages. This project serves the following purposes:

1. Demonstrate an example of using AWS SQS with Java to send and receive messages.
2. Provide an algorithm for retrying failed deliveries.
3. Provide an approach to keeping SQS costs to a minimum while maintaining real-time processing of messages.

#Goals

1. AWS SQS is [priced by request](http://aws.amazon.com/sqs/pricing/). One of the goals should be to minimize costs.
2. Processing of messages can happen on either the server that sent the message or any other server subscribing to this queue.
3. Messages are processed as soon as they are sent.

#Algorithm

There can be at least 1 receiver thread running on a given host. Each host can have any number of receiver threads.

Each receiver thread acts as follows:

    wait on the monitor object for up to visibilityTimeout
    while there are messages on the queue:
    	receive message
    	try processing message
    	if processing was succesful, delete the message
    	
Sending a message then involves the following:

    send the message
    notify all receivers waiting on the monitor object
    
Note: the above is implemented in `SQSRetryQueue.java`.

This algorithm covers the above goals:


1. Messages are processed as soon as possible as long as there are no messages ahead of them on the queue. This is guaranteed by the wait-notify mechanism, such that the host that sent the message will attempt to process messages immediately.
2. The number of unnecessary SQS requests when the queue is empty is minimized.
3. Other hosts will begin to pick up the workload after the visibilityTimeout expires.
4. Messages are processed as long as there are messages on the queue.
5. Messages that failed to process will not be deleted from the queue and will come back after `visibilityTimeout` expires.

#Running the Examples

All examples are encapsulated in the `SimpleTests.java` JUnit test case. You will need to specify `-Daws.accessKeyId=...` and `-Daws.secretKey=...`  on the command line for the JUnit tests to work.

#Using the Library

You may use this library for your own purposes by forking this github project. If you have ideas for improvement please let me know, I'll be happy to incorporate them.

#License

This project is licensed under Apache License Version 2.0. Please see `LICENSE` file more more details.
