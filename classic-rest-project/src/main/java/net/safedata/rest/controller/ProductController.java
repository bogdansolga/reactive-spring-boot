package net.safedata.rest.controller;

import net.safedata.reactive.spring.domain.Product;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class ProductController {

    private static final Random RANDOM = new Random(1000);

    private final List<Product> products = Arrays.asList(
            new Product(1, "Google Nexus 7", getRandomPrice(150)),
            new Product(2, "Apple iPad Pro", getRandomPrice(300)),
            new Product(3, "Samsung Galaxy Tab", getRandomPrice(200)),
            new Product(4, "Microsoft Surface Pro", getRandomPrice(230)),

            new Product(5, "Samsung CF791", getRandomPrice(700)),
            new Product(6, "Dell UP3218K", getRandomPrice(600)),
            new Product(7, "Samsung CH711", getRandomPrice(900)),

            new Product(8, "Lenovo Carbon X11", getRandomPrice(1000)),
            new Product(9, "Apple MacBookPro", getRandomPrice(1500)),
            new Product(10, "Razer Blade Black", getRandomPrice(2000))
    );

    @RequestMapping("/product/{id}")
    public Product getProduct(@PathVariable final int id) throws InterruptedException {
        Thread.sleep(2000);
        return products.get(id);
    }

    private double getRandomPrice(final int multiplier) {
        return RANDOM.nextDouble() * multiplier;
    }
}
