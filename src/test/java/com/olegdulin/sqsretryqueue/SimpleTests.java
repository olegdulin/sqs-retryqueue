package com.olegdulin.sqsretryqueue;

import static org.junit.Assert.*;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

import com.amazonaws.regions.Regions;

public class SimpleTests {

	@Test
	public void allFailuresRetried() throws InterruptedException {
		SQSRetryQueue srq = new SQSRetryQueue();
		srq.setSqsQueueName("TEST-allFailuresRetried");
		srq.setVisibilityTimeout(2);
		srq.setMessageReceiverThreads(4);
		srq.setRegion(Regions.US_EAST_1);
		srq.init();
		srq.startListening();
		ConcurrentHashMap<String, Boolean> messageStatus = new ConcurrentHashMap<>();
		ConcurrentHashMap<String, Boolean> retries = new ConcurrentHashMap<>();
		srq.setMessageConsumer((String messageBody) -> {
			if (!retries.containsKey(messageBody)) {
				retries.put(messageBody, true);
				throw new RuntimeException();
			}
			messageStatus.put(messageBody, true);
		});

		for (int i = 0; i < 10; i++) {
			String messageBody = "message #" + i;
			messageStatus.put(messageBody, false);
			srq.sendMessage(messageBody);
		}
		Logger.getLogger(getClass().getName()).info("Waiting for things to settle down");
		Thread.sleep(30 * 1000);
		for (Entry<String, Boolean> entry : messageStatus.entrySet()) {
			if (!entry.getValue()) {
				fail("Not all messages have been received");
			}
		}
	}

	@Test
	public void allMessagesReceived() throws InterruptedException {
		SQSRetryQueue srq = new SQSRetryQueue();
		srq.setSqsQueueName("TEST-allMessagesReceived");
		srq.setVisibilityTimeout(30);
		srq.setMessageReceiverThreads(4);
		srq.setRegion(Regions.US_EAST_1);
		srq.init();
		srq.startListening();
		ConcurrentHashMap<String, Boolean> messageStatus = new ConcurrentHashMap<>();
		srq.setMessageConsumer((String messageBody) -> {
			messageStatus.put(messageBody, true);
		});

		for (int i = 0; i < 10; i++) {
			String messageBody = "message #" + i;
			messageStatus.put(messageBody, false);
			srq.sendMessage(messageBody);
		}
		Logger.getLogger(getClass().getName()).info("Waiting for things to settle down");
		Thread.sleep(srq.getVisibilityTimeout() * 1000);
		for (Entry<String, Boolean> entry : messageStatus.entrySet()) {
			if (!entry.getValue()) {
				fail("Not all messages have been received");
			}
		}
	}

}
