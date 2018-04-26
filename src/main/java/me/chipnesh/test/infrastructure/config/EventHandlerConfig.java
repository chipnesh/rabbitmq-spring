package me.chipnesh.test.infrastructure.config;

import me.chipnesh.test.infrastructure.ack.ManualAckArgumentResolver;
import me.chipnesh.test.infrastructure.handler.EventHandlerBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.StreamListenerAnnotationBeanPostProcessor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.HandlerMethodArgumentResolversHolder;

import javax.annotation.PostConstruct;

import static org.springframework.cloud.stream.config.BindingServiceConfiguration.STREAM_LISTENER_ANNOTATION_BEAN_POST_PROCESSOR_NAME;
import static org.springframework.integration.context.IntegrationContextUtils.ARGUMENT_RESOLVERS_BEAN_NAME;

@Configuration
@EnableBinding({Sink.class, Source.class, EventHandlerSink.class})
public class EventHandlerConfig {

	private final ManualAckArgumentResolver manualAcknowledgeArgumentResolver;
	private final HandlerMethodArgumentResolversHolder holder;

	public EventHandlerConfig(
			ManualAckArgumentResolver manualAcknowledgeArgumentResolver,
			@Qualifier(ARGUMENT_RESOLVERS_BEAN_NAME) HandlerMethodArgumentResolversHolder holder
	) {
		this.manualAcknowledgeArgumentResolver = manualAcknowledgeArgumentResolver;
		this.holder = holder;
	}

	@PostConstruct
	public void init() {
		holder.addResolver(manualAcknowledgeArgumentResolver);
	}

	@Bean(STREAM_LISTENER_ANNOTATION_BEAN_POST_PROCESSOR_NAME)
	StreamListenerAnnotationBeanPostProcessor processor() {
		return new EventHandlerBeanPostProcessor();
	}

}