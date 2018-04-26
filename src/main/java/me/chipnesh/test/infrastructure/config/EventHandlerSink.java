package me.chipnesh.test.infrastructure.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

import static me.chipnesh.test.infrastructure.RetryHandler.FALLBACK;

public interface EventHandlerSink {
	@Input(FALLBACK)
	SubscribableChannel fallback();
}
