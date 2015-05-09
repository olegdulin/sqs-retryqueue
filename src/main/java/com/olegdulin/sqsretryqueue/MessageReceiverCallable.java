package com.olegdulin.sqsretryqueue;

import java.util.concurrent.Callable;

public abstract class MessageReceiverCallable implements Callable<Boolean> {

	private String messageBody;

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

}
