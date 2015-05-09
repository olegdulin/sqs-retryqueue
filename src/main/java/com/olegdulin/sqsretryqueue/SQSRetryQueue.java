package com.olegdulin.sqsretryqueue;

import javax.annotation.PostConstruct;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;

public class SQSRetryQueue<T> {

	private AmazonSQSClient sqs;
	private String sqsQueueName;
	private String queueUrl;
	private Regions region;
	private int visibilityTimeout = 10;

	public SQSRetryQueue() {

	}

	public String getSqsQueueName() {
		return sqsQueueName;
	}

	public void setSqsQueueName(String sqsQueueName) {
		this.sqsQueueName = sqsQueueName;
	}

	@PostConstruct
	public void init() {
		this.sqs = new AmazonSQSClient(); // Do we need to use new
											// ClientConfiguration().withMaxConnections(256)
											// ?
		this.sqs.configureRegion(region);
		try {
			// Check to see if queue exists
			GetQueueUrlResult queueUrlResult = this.sqs
					.getQueueUrl(getSqsQueueName());
			this.queueUrl = queueUrlResult.getQueueUrl();
		} catch (QueueDoesNotExistException queueDoesNotExist) {
			// Queue does not exist, need to create one
			CreateQueueRequest createQueueRequest = new CreateQueueRequest();
			createQueueRequest.setQueueName(getSqsQueueName());
			createQueueRequest.addAttributesEntry("VisibilityTimeout", ""
					+ getVisibilityTimeout());
			CreateQueueResult createQueueResult = this.sqs
					.createQueue(createQueueRequest);
			this.queueUrl = createQueueResult.getQueueUrl();
		}

	}

	public Regions getRegion() {
		return region;
	}

	public void setRegion(Regions region) {
		this.region = region;
	}

	public int getVisibilityTimeout() {
		return visibilityTimeout;
	}

	public void setVisibilityTimeout(int visibilityTimeout) {
		this.visibilityTimeout = visibilityTimeout;
	}
}
