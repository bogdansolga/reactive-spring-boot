package net.safedata.reactive.spring.domain.repository;

import net.safedata.reactive.spring.domain.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, Integer> {
}
