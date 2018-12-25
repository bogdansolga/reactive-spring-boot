package net.safedata.reactive.spring.config;

import net.safedata.reactive.spring.domain.entity.Product;
import net.safedata.reactive.spring.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Random;

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
        //distinctStatements();
        
        //if (true) return;

        LOGGER.info("Deleting all...");

        // every operation is executed on a different thread --> there's no sync guarantee
        productRepository.deleteAll()
                         //.subscribeOn(Schedulers.fromExecutor(Executors.newCachedThreadPool())) // can be used, to control the threads allocation
                         .thenMany(
                                 Flux.just("Samsung Apple Google HP Amazon Nook".split("\\s"))
                                     .map(name -> new Product(random.nextInt(50), name, random.nextDouble() * 1000))
                                     .flatMap(productRepository::save)
                         )
                         .thenMany(productRepository.findAll())
                         .subscribe(item -> LOGGER.info("\t[{}] {}", Thread.currentThread().getId(), item));
    }

    @SuppressWarnings("unused")
    private void distinctStatements() {
        /*
        final Flux<String> stringPublisher = Flux.just("Samsung Apple Google HP Amazon Nook".split("\\s"));
        final Flux<Product> productFlux = stringPublisher.map(name -> new Product(random.nextInt(50), name, random.nextDouble() * 1000));
        final Flux<Product> map = productFlux.flatMap(productRepository::save);
        map.subscribe(new ProductSubscriber());
        */
        //map.subscribe(System.out::println);

        //productRepository.findAll().subscribe(System.out::println);
    }
}
