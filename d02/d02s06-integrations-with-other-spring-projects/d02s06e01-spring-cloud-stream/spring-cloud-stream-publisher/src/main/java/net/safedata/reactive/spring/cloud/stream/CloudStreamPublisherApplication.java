package net.safedata.reactive.spring.cloud.stream;

import net.safedata.reactive.spring.domain.Order;
import net.safedata.reactive.spring.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootApplication
public class CloudStreamPublisherApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudStreamPublisherApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CloudStreamPublisherApplication.class, args);
	}

	@Bean
	public Supplier<Flux<Message<Order>>> producer() {
		final Stream<Message<Order>> messageStream = IntStream.rangeClosed(1, 20)
															  .mapToObj(this::buildMessage);
		return () -> Flux.fromStream(messageStream)
						 .delayElements(Duration.ofMillis(500))
						 .subscribeOn(Schedulers.boundedElastic())
						 .share();
	}

	private Message<Order> buildMessage(final int number) {
		final Product product = new Product(number, "Tablet " + number, 200 * number);
		return MessageBuilder.withPayload(new Order(number, product, LocalDateTime.now()))
							 .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
							 .build();
	}
}