package net.safedata.reactive.spring.cloud.stream;

import net.safedata.reactive.spring.domain.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Loggers;

import java.util.function.Function;

@SpringBootApplication
public class CloudStreamConsumerApplication {

	@Bean
	public Function<Flux<Order>, Mono<Void>> consumer() {
		return flux -> flux.map(order -> "[order]: " + order)
						   .log(Loggers.getLogger(CloudStreamConsumerApplication.class))
						   .then();
	}

	public static void main(String[] args) {
		SpringApplication.run(CloudStreamConsumerApplication.class, args);
	}
}
