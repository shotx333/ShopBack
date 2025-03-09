package com.shotx.shop.controller;

import com.shotx.shop.model.Product;
import com.shotx.shop.model.ProductImage;
import com.shotx.shop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all products - public access
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET product by id - public access
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST create a new product (only ADMIN allowed)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    // PUT update product
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @Valid @RequestBody Product productDetails) {
        return ResponseEntity.ok(productService.updateProduct(id, productDetails));
    }

    // DELETE product
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Upload image for a product (legacy method - uploads a single image)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<Product> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        // Use the new multiple images approach, but make this the primary image
        return ResponseEntity.ok(productService.uploadProductImage(id, file, true));
    }

    // Upload additional image for a product
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/images")
    public ResponseEntity<Product> addProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "primary", defaultValue = "false") boolean isPrimary) throws IOException {

        return ResponseEntity.ok(productService.uploadProductImage(id, file, isPrimary));
    }

    // Delete a product image
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Product> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        return ResponseEntity.ok(productService.deleteProductImage(productId, imageId));
    }

    // Set a product image as primary
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}/images/{imageId}/primary")
    public ResponseEntity<Product> setPrimaryProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        return ResponseEntity.ok(productService.setPrimaryProductImage(productId, imageId));
    }

    // Reorder product images
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}/images/reorder")
    public ResponseEntity<Product> reorderProductImages(
            @PathVariable Long productId,
            @RequestBody List<Long> imageIds) {

        return ResponseEntity.ok(productService.reorderProductImages(productId, imageIds));
    }
}