package me.chipnesh.test.infrastructure.ack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpMessageHeaderAccessor;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static me.chipnesh.test.infrastructure.EventHeaders.TIMEOUT;
import static org.springframework.amqp.support.AmqpHeaders.CHANNEL;
import static org.springframework.amqp.support.AmqpHeaders.DELIVERY_TAG;

public class ManualAck {

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(50);
	private static final Logger log = LoggerFactory.getLogger(ManualAck.class);

	private final Long deliveryTag;
	private final Channel channel;
	private final Integer timeout;
	private ScheduledFuture timer;

	public ManualAck(Message message) {
		AmqpMessageHeaderAccessor accessor = AmqpMessageHeaderAccessor.wrap(message);
		this.deliveryTag = (Long) accessor.getHeader(DELIVERY_TAG);
		this.channel = (Channel) accessor.getHeader(CHANNEL);
		this.timeout = (Integer) accessor.getHeader(TIMEOUT);
		initTimeoutHandler();
	}

	private synchronized void initTimeoutHandler() {
		CompletableFuture<Void> timeoutHandler = new CompletableFuture<>();
		timer = scheduler.schedule(() -> {
			timeoutHandler.complete(null);
		}, timeout, SECONDS);
		timeoutHandler.thenRun(this::reject);
		channel.addShutdownListener(cause -> timer.cancel(true));
	}

	public synchronized Void accept() {
		try {
			ShutdownSignalException closeReason = channel.getConnection().getCloseReason();
			if (!timer.isCancelled() && closeReason == null) {
				timer.cancel(true);
				channel.basicAck(deliveryTag, false);
			}
		} catch (IOException e) {
			log.error("Can't accept rabbit message", e);
		}
		return null;
	}

	public synchronized Void reject() {
		try {
			if (!timer.isCancelled()) {
				timer.cancel(true);
				channel.basicNack(deliveryTag, false, false);
			}
		} catch (Exception e) {
			log.error("Can't reject rabbit message", e);
		}
		return null;
	}
}
