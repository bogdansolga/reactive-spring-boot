package net.safedata.reactive.spring.cloud.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudStreamConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(net.safedata.reactive.spring.integration.ConsumerApplication.class, args);
	}
}
