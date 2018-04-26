package me.chipnesh.test.infrastructure.handler;

import me.chipnesh.test.domain.Event;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.StreamListenerAnnotationBeanPostProcessor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static me.chipnesh.test.infrastructure.EventHeaders.EVENT_TYPE;
import static org.springframework.core.annotation.AnnotationUtils.*;
import static org.springframework.util.StringUtils.hasText;

public class EventHandlerBeanPostProcessor extends StreamListenerAnnotationBeanPostProcessor {

	private static final String EVENT_HANDLER_SPEL_PATTERN = "headers['" + EVENT_TYPE + "']=='%s'";

	@Override
	protected StreamListener postProcessAnnotation(StreamListener originalAnnotation, Method annotatedMethod) {
		Map<String, Object> attributes = new HashMap<>(getAnnotationAttributes(originalAnnotation));

		if (isEventHandler(annotatedMethod)) {
			Stream.of(annotatedMethod.getParameters())
					.map(Parameter::getType)
					.filter(Event.class::isAssignableFrom)
					.findFirst().ifPresent(type -> applyConditions(attributes, type));
		}

		return applyAttributes(annotatedMethod, attributes);
	}

	@SuppressWarnings("ConstantConditions")
	private boolean isEventHandler(Method annotatedMethod) {
		return getAnnotation(annotatedMethod, EventHandler.class) != null;
	}

	private void applyConditions(Map<String, Object> attributes, Class<?> type) {
		String finalCondition = format(EVENT_HANDLER_SPEL_PATTERN, type.getSimpleName());

		String condition = getConditionFrom(attributes);
		if (hasText(condition)) {
			finalCondition += " && " + condition;
		}

		attributes.put("condition", finalCondition);
	}

	private String getConditionFrom(Map<String, Object> attributes) {
		return ofNullable(attributes.get("condition"))
				.map(String.class::cast)
				.orElse(null);
	}

	private StreamListener applyAttributes(Method annotatedMethod, Map<String, Object> attributes) {
		return synthesizeAnnotation(attributes, StreamListener.class, annotatedMethod);
	}
}
