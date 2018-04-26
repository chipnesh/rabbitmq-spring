package me.chipnesh.test.infrastructure.handler;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@StreamListener
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EventHandler {

	@AliasFor(annotation = StreamListener.class, attribute = "target")
	String value() default Sink.INPUT;
}
