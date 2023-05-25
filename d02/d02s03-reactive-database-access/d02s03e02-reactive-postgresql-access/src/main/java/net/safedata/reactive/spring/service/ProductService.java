package net.safedata.reactive.spring.service;

import net.safedata.reactive.spring.domain.entity.Product;
import net.safedata.reactive.spring.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Double> getTotalProductsPrice() {
        return productRepository.findAll()
                                .doOnNext(item -> System.out.println(item.getId() + " - " + item.getPrice()))
                                .map(Product::getPrice)
                .parallel()
                                .reduce(Double::sum);
    }
}
