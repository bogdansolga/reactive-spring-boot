package net.safedata.reactive.spring.cloud.stream;

import net.safedata.reactive.spring.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageChannel;
import reactor.core.publisher.Flux;

@SpringBootApplication
//@EnableBinding(ConsumerChannels.class) 	--> more specific
@EnableBinding(Sink.class) // 				--> more generic
public class CloudStreamConsumerApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudStreamConsumerApplication.class);

	// will be called once, when the app starts, and it will continue to process data that arrives
	@StreamListener
	public void process(@Input(Sink.INPUT) final Flux<Order> ordersFlux) {
		ordersFlux.map(order -> "[order]: " + order)
				  .subscribe(order -> LOGGER.info("{}", order));
	}

	/*
	@StreamListener
	public void process(@Input(Sink.INPUT) final Mono<Order> order) {

	}
	*/

	public static void main(String[] args) {
		SpringApplication.run(CloudStreamConsumerApplication.class, args);
	}
}

@SuppressWarnings("unused")
interface ConsumerChannels {
	String ORDERS = "orders";

	@Input(ORDERS)
	MessageChannel orders();
}
