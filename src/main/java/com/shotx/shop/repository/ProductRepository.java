package com.shotx.shop.repository;

import com.shotx.shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Search products by name, description, or category name
     *
     * @param query The search term
     * @return List of products matching the search criteria
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.category c " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);

    /**
     * Search products by name, description, or category name within specific categories
     *
     * @param query The search term
     * @param categoryIds List of category IDs to filter by
     * @return List of products matching the search criteria and categories
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.category c " +
            "WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND c.id IN :categoryIds")
    List<Product> searchProductsByCategories(@Param("query") String query, @Param("categoryIds") List<Long> categoryIds);
}