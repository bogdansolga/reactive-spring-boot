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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class ReactiveWebClientExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveWebClientExample.class);

    private final WebClient webClient = WebClient.create("http://localhost:8080");

    private final int productsCount = 10;

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(ReactiveWebClientExample.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            fluxReturningCall();
            if (true) return;

            final Instant start = Instant.now();
            sequentialCalls();
            parallelCallsUsingStreams();
            parallelCallsUsingFluxRangeUsingProcessing();
            parallelCallsUsingFluxRangeWithoutProcessing();

            System.out.println();
            LOGGER.info("Getting all the {} products took {} ms", productsCount, Duration.between(start, Instant.now()).toMillis());
        };
    }

    private void fluxReturningCall() {
        webClient.get()
                 .uri("/product")
                 .retrieve()
                 .bodyToFlux(Product.class)
                 .doFinally(item -> System.out.println(item))
                 .subscribe(
                         item -> System.out.println(item),
                         err -> err.printStackTrace(),
                         () -> System.out.println("On complete")
                 );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sequentialCalls() {
        final List<Product> products = IntStream.range(0, productsCount)
                                               .boxed()
                                               .map(id -> webClient.get()
                                                                   .uri("/product/{id}", id)
                                                                   .retrieve()
                                                                   .bodyToMono(Product.class)
                                                                   .block())
                                               .collect(Collectors.toList());
        LOGGER.info("Got {} products", products.size());
    }

    private void parallelCallsUsingStreams() {
        final List<Mono<Product>> products = IntStream.range(0, productsCount)
                                                      .boxed()
                                                      .map(id -> webClient.get()
                                                                          .uri("/product/{id}", id)
                                                                          .retrieve()
                                                                          .bodyToMono(Product.class))
                                                      .collect(Collectors.toList());

        Mono.when(products)
            .block();
    }

    private void parallelCallsUsingFluxRangeUsingProcessing() {
        Flux.range(0, productsCount)
            .flatMap(id -> webClient.get()
                                    .uri("/product/{id}", id)
                                    // if processing needs to be done before getting the body
                                    // --> perhaps it's very large, or needing to access the HTTP status or headers
                                    .exchange()
                                    .flatMap(response -> response.bodyToMono(Product.class))) // can also nest other client calls, if needed
            .blockLast();
    }

    private void parallelCallsUsingFluxRangeWithoutProcessing() {
        Flux.range(0, productsCount)
            .flatMap(id -> webClient.get()
                                    .uri("/product/{id}", id)
                                    .retrieve() // gives a straight path to the response body
                                    .bodyToMono(Product.class))
            .blockLast();
    }
}
