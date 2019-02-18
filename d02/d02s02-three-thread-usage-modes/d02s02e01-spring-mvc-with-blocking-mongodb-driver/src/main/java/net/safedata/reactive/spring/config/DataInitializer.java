package net.safedata.reactive.spring.config;

import net.safedata.reactive.spring.domain.entity.Product;
import net.safedata.reactive.spring.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final Random random = new Random(500);

    private final ProductRepository productRepository;

    @Autowired
    public DataInitializer(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        final AtomicInteger atomicInteger = new AtomicInteger(1);
        Arrays.asList("Samsung Apple Google HP Amazon".split("\\s"))
              .forEach(item -> productRepository.save(new Product(atomicInteger.getAndIncrement(), item, 200)));
    }
}
