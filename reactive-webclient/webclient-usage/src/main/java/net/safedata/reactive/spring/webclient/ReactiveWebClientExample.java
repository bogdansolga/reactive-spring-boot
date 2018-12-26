package net.safedata.reactive.spring.webclient;

import net.safedata.reactive.spring.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class ReactiveWebClientExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveWebClientExample.class);

    private static WebClient webClient = WebClient.create("http://localhost:8080");

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(ReactiveWebClientExample.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println();

            final int productsCount = 10;

            final Instant start = Instant.now();
            final List<Mono<Product>> products = IntStream.range(0, productsCount)
                                                          .boxed()
                                                          .map(id -> webClient.get()
                                                                              .uri("/product/{id}", id)
                                                                              .retrieve()
                                                                              .bodyToMono(Product.class))
                                                          .collect(Collectors.toList());

            Mono.when(products)
                .block();

            System.out.println();
            LOGGER.info("Getting all the {} products took {} ms", productsCount, Duration.between(start, Instant.now()).toMillis());
        };
    }
}
