package net.safedata.reactive.spring.domain.repository;

import net.safedata.reactive.spring.domain.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {
}
