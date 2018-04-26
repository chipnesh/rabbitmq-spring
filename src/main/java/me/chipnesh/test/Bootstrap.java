package me.chipnesh.test;

import me.chipnesh.test.domain.HelloEvent;
import me.chipnesh.test.infrastructure.handler.EnableEventHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEventHandling
public class Bootstrap {

	private final MyEventPublisher publisher;

	@Autowired
	public Bootstrap(MyEventPublisher publisher) {
		this.publisher = publisher;
	}

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Bootstrap.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

	@Bean
	public CommandLineRunner runner() {
		return args -> publisher.publish(new HelloEvent("Hello"));
	}
}
