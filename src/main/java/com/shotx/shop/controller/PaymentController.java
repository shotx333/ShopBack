package com.shotx.shop.controller;

import com.shotx.shop.model.Order;
import com.shotx.shop.repository.OrderRepository;
import com.shotx.shop.service.OrderService;
import com.shotx.shop.service.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderRepository orderRepository, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @PostMapping("/create-payment-intent/{orderId}")
    public ResponseEntity<?> createPaymentIntent(@PathVariable Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            String clientSecret = paymentService.createPaymentIntent(order);

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", clientSecret);

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating payment intent: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            String eventType = paymentService.handleWebhook(payload, sigHeader);

            // Handle different event types
            if ("payment_intent.succeeded".equals(eventType)) {
                // Payment was successful - we would update the order status here
                return ResponseEntity.ok("Payment processed successfully");
            }

            return ResponseEntity.ok("Webhook received");
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Webhook error: " + e.getMessage());
        }
    }
}