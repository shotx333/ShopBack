package com.shotx.shop.service;

import com.shotx.shop.model.Order;
import com.shotx.shop.model.OrderItem;
import com.shotx.shop.model.Product;
import com.shotx.shop.repository.OrderRepository;
import com.shotx.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Transactional
    public Order placeOrder(String username, List<OrderItem> items) {
        // First check if all products have sufficient stock
        for (OrderItem item : items) {
            if (!productService.checkStock(item.getProductId(), item.getQuantity())) {
                throw new RuntimeException("Not enough stock for product ID: " + item.getProductId());
            }
        }

        // Calculate total and set order reference for each item.
        double total = 0.0;
        for (OrderItem item : items) {
            // Retrieve product info to calculate price.
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            double itemPrice = product.getPrice() * item.getQuantity();
            item.setPrice(product.getPrice());
            total += itemPrice;

            // Decrease stock for this item
            productService.decreaseStock(item.getProductId(), item.getQuantity());
        }

        Order order = new Order();
        order.setUsername(username);
        order.setItems(items);
        order.setTotalPrice(total);

        // Set the back-reference for each order item.
        for (OrderItem item : items) {
            item.setOrder(order);
        }

        return orderRepository.save(order);
    }

    public List<Order> getOrdersForUser(String username) {
        return orderRepository.findByUsername(username);
    }
}