package net.safedata.reactive.spring.integration.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.Duration;
import java.util.Date;

@SpringBootApplication
public class ConsumerApplication {

	@Bean
	IntegrationFlow integrationFlow() {
		final Flux<Date> generate = Flux.<Date>generate(sink -> sink.next(new Date()))
				.delayElements(Duration.ofSeconds(2));

		return IntegrationFlows
				.from(generate.map(payload -> MessageBuilder.withPayload(payload).build()))
				//.split()
				//.route()
				//.transform()
				//.aggregate()
				.handle((GenericHandler<Date>) (payload, headers) -> {
					displayMessage(payload, headers);
					return null;
				})
				//.transform(outputChannel())
				.handle(Files.outboundAdapter(new File("/Users/bogdan/dates.txt")))
				.get();
	}

	@Bean
	MessageChannel outputChannel() {
		return MessageChannels.direct().get();
	}

	private void displayMessage(final Date date, final MessageHeaders messageHeaders) {
		System.out.println("The date is " + date);
		/*
		messageHeaders.values()
					  .forEach(value -> System.out.println("\t" + value));
		*/
	}

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}
}
