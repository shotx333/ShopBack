package com.shotx.shop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    // Is this the main/primary image for the product
    @Column(name = "is_primary") // Use backticks to escape the reserved keyword
    private boolean isPrimary = false;

    // Display order for the images
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    // Constructors
    public ProductImage() {}

    public ProductImage(String imageUrl, Product product) {
        this.imageUrl = imageUrl;
        this.product = product;
    }

    public ProductImage(String imageUrl, Product product, boolean isPrimary, Integer displayOrder) {
        this.imageUrl = imageUrl;
        this.product = product;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean primary) {
        this.isPrimary = primary;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}