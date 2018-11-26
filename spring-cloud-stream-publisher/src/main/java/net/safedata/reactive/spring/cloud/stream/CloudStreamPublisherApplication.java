package net.safedata.reactive.spring.cloud.stream;

import net.safedata.reactive.spring.domain.Order;
import net.safedata.reactive.spring.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

import java.time.LocalDateTime;

@SpringBootApplication
//@EnableBinding(PublisherChannels.class) 	--> more specific
@EnableBinding(Source.class) // a generic producer channel
public class CloudStreamPublisherApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudStreamPublisherApplication.class);

	@Bean
	ApplicationRunner producer(final Source source) {
		return args -> {
			int number = 0;
			while (number < 100) {
				sendOrderAsMessage(source, number++);
				Thread.sleep(1000);
			}
		};
	}

	private void sendOrderAsMessage(final Source source, final int number) {
		final Product product = new Product(number, "Tablet " + number, 200 * number);
		final Message<Order> orderMessage = MessageBuilder.withPayload(new Order(number, product, LocalDateTime.now()))
														   .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
														   .build();
		LOGGER.info("Sending a message for the order '{}'...", orderMessage.getPayload());
		source.output().send(orderMessage);
	}

	public static void main(String[] args) {
		SpringApplication.run(CloudStreamPublisherApplication.class, args);
	}
}

@SuppressWarnings("unused")
interface PublisherChannels {
	String ORDERS = "orders";

	@Output(ORDERS)
	MessageChannel output();
}