package com.shotx.shop.service;

import com.shotx.shop.model.Product;
import com.shotx.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());

        // If stock is provided in the update, update it
        if (productDetails.getStock() != null) {
            product.setStock(productDetails.getStock());
        }

        // If image URL is provided, update it
        if (productDetails.getImageUrl() != null) {
            product.setImageUrl(productDetails.getImageUrl());
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Checks if there is sufficient stock for a product
     * @param productId Product ID
     * @param requestedQuantity Quantity requested
     * @return True if sufficient stock exists, false otherwise
     */
    public boolean checkStock(Long productId, int requestedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return product.getStock() >= requestedQuantity;
    }

    /**
     * Decreases the stock of a product by the given quantity
     * @param productId Product ID
     * @param quantity Quantity to decrease
     * @return Updated product
     * @throws RuntimeException If insufficient stock
     */
    @Transactional
    public Product decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.decreaseStock(quantity)) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        return productRepository.save(product);
    }
}