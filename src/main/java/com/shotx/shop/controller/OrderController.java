package com.shotx.shop.controller;

import com.shotx.shop.model.Order;
import com.shotx.shop.model.OrderItem;
import com.shotx.shop.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> placeOrder(@RequestBody List<OrderItem> items, Authentication authentication) {
        // Retrieve the username from the security context.
        String username = authentication.getName();
        Order order = orderService.placeOrder(username, items);

        // Return both the order and the checkout URL/info
        Map<String, Object> response = new HashMap<>();
        response.put("order", order);
        response.put("checkoutUrl", "/checkout/" + order.getId());

        return ResponseEntity.ok(response);
    }

    // Endpoint to get all orders for the authenticated user.
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        String username = authentication.getName();
        List<Order> orders = orderService.getOrdersForUser(username);
        return ResponseEntity.ok(orders);
    }

    // Get a specific order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Order order = orderService.getOrderById(id, username);
        return ResponseEntity.ok(order);
    }

    // Update order payment status - changed from @PatchMapping to @PostMapping
    @PostMapping("/{id}/payment-status")
    public ResponseEntity<Order> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Order.PaymentStatus status,
            Authentication authentication) {

        String username = authentication.getName();
        Order updatedOrder = orderService.updatePaymentStatus(id, status, username);
        return ResponseEntity.ok(updatedOrder);
    }
}