package net.safedata.reactive.spring.config;

import net.safedata.reactive.spring.domain.StoreSetup;
import net.safedata.reactive.spring.domain.entity.Product;
import net.safedata.reactive.spring.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Random;

@Component
public class DataInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final Random random = new Random(500);

    @Bean
    public ApplicationRunner applicationRunner(final ProductRepository productRepository) {
        return args ->
            productRepository.deleteAll()
                             .publishOn(Schedulers.parallel())
                             .thenMany(Flux.fromStream(StoreSetup.getProductNames().stream())
                                           .flatMap(item -> productRepository.save(createProduct(item))))
                             .thenMany(productRepository.count())
                             .subscribe(value -> LOGGER.info("There are {} products in the MongoDB database", value));
    }

    private Product createProduct(String productName) {
        return new Product(random.nextInt(50), productName, 200);
    }
}
