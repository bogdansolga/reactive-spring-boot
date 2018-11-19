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
}
