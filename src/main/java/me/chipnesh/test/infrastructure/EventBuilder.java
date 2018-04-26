package me.chipnesh.test.infrastructure;

import me.chipnesh.test.domain.Event;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

public class EventBuilder<T extends Event> {
	private static String eventType;
	private T payload;
	private int retryCount = 1;
	private int timeout = 10;

	public EventBuilder(T payload) {
		this.payload = payload;
	}

	public static <T extends Event> EventBuilder<T> event(T payload) {
		eventType = payload.getClass().getSimpleName();
		return new EventBuilder<>(payload);
	}

	public EventBuilder<T> withRetryCount(int retryCount) {
		Assert.isTrue(retryCount >= 1, "retryCount should be at least 1");
		this.retryCount = retryCount;
		return this;
	}

	public EventBuilder<T> withTimeout(int timeout) {
		Assert.isTrue(timeout >= 1, "timeout should be at least 1 sec");
		this.timeout = timeout;
		return this;
	}

	public Message<T> build() {
		return MessageBuilder
				.withPayload(payload)
				.setHeader(EventHeaders.EVENT_TYPE, eventType)
				.setHeader(EventHeaders.RETRY_COUNT, retryCount)
				.setHeader(EventHeaders.TIMEOUT, timeout)
				.build();
	}
}
