package net.safedata.reactive.spring.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Order implements Serializable {

    private final int id;
    private final List<Product> products;
    private final LocalDateTime orderDatetime;

    public Order(final int id, final List<Product> products, final LocalDateTime orderDatetime) {
        this.id = id;
        this.products = products;
        this.orderDatetime = orderDatetime;
    }

    public Order(final int id, final Product product, final LocalDateTime orderDatetime) {
        this.id = id;
        this.products = Collections.singletonList(product);
        this.orderDatetime = orderDatetime;
    }

    public int getId() {
        return id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public LocalDateTime getOrderDatetime() {
        return orderDatetime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", products=" + products.size() + " products " +
                ", orderDatetime=" + orderDatetime +
                '}';
    }
}
