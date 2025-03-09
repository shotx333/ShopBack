package com.shotx.shop.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Product price is required")
    @Min(value = 0, message = "Price must be a positive value")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull(message = "Category is required")
    private Category category;

    // Legacy field for backwards compatibility
    private String imageUrl;

    // Stock/inventory field
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stock = 0;

    // New relationship for multiple images
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductImage> images = new ArrayList<>();

    // Constructors
    public Product() {}

    public Product(String name, String description, Double price, Category category, Integer stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = Double.parseDouble(String.format("%.2f", price));
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        // If we have a primary image in the collection, use that
        ProductImage primaryImage = this.images.stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .orElse(null);

        if (primaryImage != null) {
            return primaryImage.getImageUrl();
        }

        // Otherwise, use the legacy field
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    // Add image to product
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    // Remove image from product
    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }

    // Helper method to decrease stock
    public boolean decreaseStock(int quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
            return true;
        }
        return false;
    }
}