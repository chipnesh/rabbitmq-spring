package me.chipnesh.test;

import me.chipnesh.test.domain.Event;
import me.chipnesh.test.infrastructure.EventBuilder;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.stereotype.Component;

@Component
public class MyEventPublisher {

	private final Source source;

	public MyEventPublisher(Source source) {
		this.source = source;
	}

	public void publish(Event event) {
		source.output().send(EventBuilder.event(event).build());
	}
}
