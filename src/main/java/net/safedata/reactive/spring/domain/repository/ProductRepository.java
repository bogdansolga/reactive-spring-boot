package net.safedata.reactive.spring.domain.repository;

import net.safedata.reactive.spring.domain.entity.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, Integer> {
}
