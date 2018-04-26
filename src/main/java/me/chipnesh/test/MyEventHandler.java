package me.chipnesh.test;

import me.chipnesh.test.domain.ByeEvent;
import me.chipnesh.test.domain.HelloEvent;
import me.chipnesh.test.infrastructure.EventHeaders;
import me.chipnesh.test.infrastructure.handler.EventHandler;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chipnesh.test.infrastructure.RetryHandler.FALLBACK;

@Component
public class MyEventHandler {

	private final MyEventPublisher publisher;

	public MyEventHandler(MyEventPublisher publisher) {
		this.publisher = publisher;
	}

	@EventHandler
	public void handleHello(HelloEvent event) {
		publisher.publish(new ByeEvent(":("));
	}

	@EventHandler
	public void handleByeBye(ByeEvent event) {
		System.out.println("bye event = " + event);
		throw new RuntimeException("fuck");
	}

	@EventHandler(FALLBACK)
	public void handleHelloFailed(HelloEvent event, @Headers Map<String, ?> headers) {
		System.out.println(headers.get(EventHeaders.STACK_TRACE));
		System.out.println(headers.get(EventHeaders.ERROR_MESSAGE));
		System.out.println("Fallback for " + event);
	}

	@EventHandler(FALLBACK)
	public void handleByeByeFailed(ByeEvent event, @Headers Map<String, ?> headers) {
		System.out.println(headers.get(EventHeaders.STACK_TRACE));
		System.out.println(headers.get(EventHeaders.ERROR_MESSAGE));
		System.out.println("Fallback for " + event);
		publisher.publish(new HelloEvent(":)"));
	}
}
