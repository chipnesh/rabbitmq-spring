package me.chipnesh.test.infrastructure;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;
import static me.chipnesh.test.infrastructure.EventHeaders.CURRENT_RETRY;
import static me.chipnesh.test.infrastructure.EventHeaders.RETRY_COUNT;

@Component
public class RetryHandler {
	public static final String DLQ = "dlq";
	public static final String FALLBACK = "fallback";

	private final RabbitTemplate rabbitTemplate;

	@Value(FALLBACK + ".${spring.application.name}")
	private String fallbackQueueName;

	public RetryHandler(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@RabbitListener(queues = DLQ)
	public void rePublish(Message failedMessage) {
		MessageProperties properties = failedMessage.getMessageProperties();
		Integer currentRetry = getOrDefault(properties, CURRENT_RETRY, 1);
		int retryCount = getOrDefault(properties, RETRY_COUNT, 3);
		if (currentRetry < retryCount) {
			incrementRetry(properties);
			tryAgain(failedMessage);
		} else {
			fallback(failedMessage);
		}
	}

	private void tryAgain(Message failedMessage) {
		MessageProperties properties = failedMessage.getMessageProperties();
		String originalQueueName = properties.getReceivedRoutingKey();
		Message repeatMessage = MessageBuilder.fromClonedMessage(failedMessage).build();
		rabbitTemplate.send(originalQueueName, repeatMessage);
	}

	private void fallback(Message failedMessage) {
		Message fallbackMessage = MessageBuilder.fromClonedMessage(failedMessage).build();
		rabbitTemplate.send(fallbackQueueName, fallbackMessage);
	}

	@SuppressWarnings("unchecked")
	private <T> T getOrDefault(MessageProperties messageProperties, String header, T defaultValue) {
		return (T) ofNullable(messageProperties
				.getHeaders()
				.get(header))
				.orElse(defaultValue);
	}

	private void incrementRetry(MessageProperties properties) {
		Integer currentRetry = getOrDefault(properties, CURRENT_RETRY, 1);
		properties.getHeaders().put(CURRENT_RETRY, currentRetry + 1);
	}
}
