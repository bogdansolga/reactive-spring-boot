package net.safedata.reactive.spring.service;

import net.safedata.reactive.spring.domain.entity.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

@Service
public class ProductService {

    public Flux<Product> productsStream() {
        return Flux.<Product>generate(sink -> sink.next(new Product(10, "The product generated at " + Instant.now(), 30)))
                   .delayElements(Duration.ofSeconds(1));
    }

    public Flux<Product> someProducts() {
        return Flux.just(
                new Product(1, "The first product", 100),
                new Product(2, "The second product", 200),
                new Product(3, "The third product", 300)
        );
    }
}
