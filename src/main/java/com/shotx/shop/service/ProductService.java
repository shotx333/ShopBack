package com.shotx.shop.service;

import com.shotx.shop.model.Product;
import com.shotx.shop.model.ProductImage;
import com.shotx.shop.repository.ProductImageRepository;
import com.shotx.shop.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final String uploadDir = "uploads";

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;

        // Create uploads directory if it doesn't exist
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory");
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Search for products by name, description, or category
     *
     * @param query The search term
     * @return List of products matching the search criteria
     */
    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchProducts(query.trim());
    }

    /**
     * Search for products by name, description, or category within specified categories
     *
     * @param query The search term
     * @param categoryIds List of category IDs to filter by
     * @return List of products matching the search criteria and categories
     */
    public List<Product> searchProductsByCategories(String query, List<Long> categoryIds) {
        if (query == null || query.trim().isEmpty()) {
            // If no search query but category filters are present
            if (categoryIds != null && !categoryIds.isEmpty()) {
                return productRepository.findAll().stream()
                        .filter(p -> p.getCategory() != null && categoryIds.contains(p.getCategory().getId()))
                        .toList();
            }
            return getAllProducts();
        }

        if (categoryIds == null || categoryIds.isEmpty()) {
            return searchProducts(query);
        }

        return productRepository.searchProductsByCategories(query.trim(), categoryIds);
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

        // Legacy imageUrl support
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

    /**
     * Upload an image for a product and set it as primary if specified
     * @param productId Product ID
     * @param file Image file to upload
     * @param isPrimary Whether this image should be the primary product image
     * @return The updated product
     * @throws IOException If there's an error saving the file
     */

//    @Transactional
//    public Product uploadProductImage(Long productId, MultipartFile file, boolean isPrimary) throws IOException {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        // Generate a unique filename to avoid conflicts
//        String originalFilename = file.getOriginalFilename();
//        String extension = "";
//        if (originalFilename != null && originalFilename.contains(".")) {
//            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        }
//        String filename = UUID.randomUUID().toString() + extension;
//
//        // Save the file
//        Path filePath = Paths.get(uploadDir, filename);
//        Files.copy(file.getInputStream(), filePath);
//
//        // Create a new product image
//        String imageUrl = "/uploads/" + filename;
//
//        // If this is set as primary, update any existing primary images
//        if (isPrimary) {
//            product.getImages().forEach(img -> img.setIsPrimary(false));
//        }
//
//        // Determine the display order (simply add it at the end)
//        int displayOrder = product.getImages().size();
//
//        ProductImage productImage = new ProductImage(imageUrl, product, isPrimary, displayOrder);
//        product.addImage(productImage);
//
//        // For backward compatibility, also set the imageUrl field if this is the primary image
//        if (isPrimary) {
//            product.setImageUrl(imageUrl);
//        }
//
//        return productRepository.save(product);
//    }

    @Transactional(rollbackFor = Exception.class)
    public Product uploadProductImage(Long productId,
                                      MultipartFile file,
                                      boolean isPrimary) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // — Generate a unique filename & write to disk —
        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(n -> n.contains("."))
                .map(n -> n.substring(n.lastIndexOf(".")))
                .orElse("");
        String filename = UUID.randomUUID() + ext;
        Path target = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        String imageUrl = "/uploads/" + filename;

        // — If this is primary, clear the old one in‑memory —
        if (isPrimary) {
            product.getImages().stream()
                    .filter(ProductImage::isIsPrimary)
                    .forEach(pi -> pi.setIsPrimary(false));
        }

        // — Build the new image and hook it up —
        ProductImage img = new ProductImage();
        img.setImageUrl(imageUrl);
        img.setDisplayOrder(product.getImages().size());
        img.setIsPrimary(isPrimary);
        product.addImage(img);

        // — Legacy single‑image support —
        if (isPrimary) {
            product.setImageUrl(imageUrl);
        }

        // **Only save the product** (cascade will persist the new image)
        return productRepository.save(product);
    }

    /**
     * Delete a product image
     * @param productId Product ID
     * @param imageId Image ID to delete
     * @return The updated product
     */
    @Transactional
    public Product deleteProductImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductImage imageToDelete = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Ensure the image belongs to the product
        if (!imageToDelete.getProduct().getId().equals(product.getId())) {
            throw new RuntimeException("Image does not belong to this product");
        }

        // If this was the primary image, assign a new primary image if there are other images
        boolean wasPrimary = imageToDelete.isIsPrimary();

        // Remove the image
        product.removeImage(imageToDelete);
        productImageRepository.delete(imageToDelete);

        // Try to delete the file
        try {
            String filename = imageToDelete.getImageUrl().substring(imageToDelete.getImageUrl().lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Just log this error, don't throw it
            System.err.println("Could not delete image file: " + e.getMessage());
        }

        // If the deleted image was primary and there are other images, set the first one as primary
        if (wasPrimary && !product.getImages().isEmpty()) {
            ProductImage newPrimary = product.getImages().get(0);
            newPrimary.setIsPrimary(true);

            // Update the legacy imageUrl as well
            product.setImageUrl(newPrimary.getImageUrl());
        } else if (product.getImages().isEmpty()) {
            // No more images, clear the legacy imageUrl
            product.setImageUrl(null);
        }

        // Reorder the remaining images
        for (int i = 0; i < product.getImages().size(); i++) {
            product.getImages().get(i).setDisplayOrder(i);
        }

        return productRepository.save(product);
    }

    /**
     * Set a product image as the primary image
     * @param productId Product ID
     * @param imageId Image ID to set as primary
     * @return The updated product
     */
    @Transactional
    public Product setPrimaryProductImage(Long productId, Long imageId) {
        // 1) sanity‑check product + image
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        ProductImage toSet = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));
        if (!toSet.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Image does not belong to this product");
        }

        // 2) bulk‐clear old primary flags
        productImageRepository.clearPrimaryFlags(productId);

        // 3) set the new primary
        productImageRepository.setPrimaryFlag(imageId);

        // 4) update legacy imageUrl on the product
        product.setImageUrl(toSet.getImageUrl());

        // 5) save & flush the product so you also see the UPDATE on product.image_url
        return productRepository.saveAndFlush(product);
    }
    /**
     * Reorder product images
     * @param productId Product ID
     * @param imageIds Ordered list of image IDs
     * @return The updated product
     */
    @Transactional
    public Product reorderProductImages(Long productId, List<Long> imageIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Ensure all images belong to the product
        if (product.getImages().size() != imageIds.size()) {
            throw new RuntimeException("Image IDs don't match product's images");
        }

        // Create a map of image IDs to display orders
        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);

            // Find the image in the product's collection
            ProductImage image = product.getImages().stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Image not found in product"));

            image.setDisplayOrder(i);
        }

        return productRepository.save(product);
    }
}