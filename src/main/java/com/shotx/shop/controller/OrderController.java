package com.shotx.shop.controller;

import com.shotx.shop.model.Order;
import com.shotx.shop.model.OrderItem;
import com.shotx.shop.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint to place an order.
    // Expects a JSON array of order items in the request body.
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody List<OrderItem> items, Authentication authentication) {
        // Retrieve the username from the security context.
        String username = authentication.getName();
        Order order = orderService.placeOrder(username, items);
        return ResponseEntity.ok(order);
    }

    // Endpoint to get all orders for the authenticated user.
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        String username = authentication.getName();
        List<Order> orders = orderService.getOrdersForUser(username);
        return ResponseEntity.ok(orders);
    }
}
