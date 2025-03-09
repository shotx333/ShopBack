package com.shotx.shop.repository;

import com.shotx.shop.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);

    ProductImage findByProductIdAndPrimaryIsTrue(Long productId);
}