import net.safedata.reactive.spring.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;


public class WebClientTest {

    private final WebClient webClient = WebClient.create("http://localhost:8080");

    @Test
    public void givenThereAreTenProducts_whenGettingTheProducts_thenTheNumberIsCorrectAndOnCompleteIsReceived() {
        final Flux<Product> productFlux = webClient.get()
                                                   .uri("/product")
                                                   .retrieve()
                                                   .bodyToFlux(Product.class);

        StepVerifier.create(productFlux)
                    .expectSubscription()
                    .thenAwait(Duration.ofSeconds(1))
                    .expectNextCount(10)
                    .verifyComplete();

        WebTestClient webTestClient = WebTestClient.bindToServer()
                                                   .baseUrl("http://localhost:8080")
                                                   .build();
        webTestClient.get()
                     .uri("/product")
                     .exchange()
                     .expectBody();
    }

    @Test
    public void testPublisherTest() {
        TestPublisher<String> test = TestPublisher.<String>create();

        StepVerifier.create(test)
                    .then(() -> test.emit("first", "second"))
                    .then(() -> test.complete())
                    .expectNextCount(2)
                    .verifyComplete();
    }
}
