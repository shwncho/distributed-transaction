package com.example.monolithic.product.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;

    private Long price;

    public Product() {

    }

    public Product(Long quantity, Long price) {
        this.quantity = quantity;
        this.price = price;
    }
}
