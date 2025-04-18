package com.shotx.shop.repository;

import com.shotx.shop.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // clear any existing primary for this product
    @Modifying
    @Query("""
        UPDATE ProductImage pi
          SET pi.isPrimary = false
        WHERE pi.product.id = :productId
          AND pi.isPrimary = true
        """)
    int clearPrimaryFlags(@Param("productId") Long productId);

    // mark the chosen image as primary
    @Modifying
    @Query("""
        UPDATE ProductImage pi
          SET pi.isPrimary = true
        WHERE pi.id = :imageId
        """)
    int setPrimaryFlag(@Param("imageId") Long imageId);

    List<ProductImage> findByProductId(Long productId);

    ProductImage findByProductIdAndIsPrimaryIsTrue(Long productId);
}
