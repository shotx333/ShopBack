package com.shotx.shop.repository;

import com.shotx.shop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find orders by username
    List<Order> findByUsername(String username);
}
