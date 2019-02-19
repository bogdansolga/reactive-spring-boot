package net.safedata.reactive.spring.domain.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class Product implements Serializable {

    @Id
    private int id;

    private String name;

    private double price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
