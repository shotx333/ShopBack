package com.shotx.shop.service;

import com.shotx.shop.model.Order;
import com.shotx.shop.model.OrderItem;
import com.shotx.shop.model.Product;
import com.shotx.shop.repository.ProductRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final ProductRepository productRepository;

    public PaymentService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Create a payment intent for an order
     * @param order The order to create a payment intent for
     * @return The client secret for the payment intent
     * @throws StripeException If there's an error with Stripe
     */
    public String createPaymentIntent(Order order) throws StripeException {
        // Calculate the order amount using items in the order
        long amount = calculateOrderAmount(order.getItems());

        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setCurrency("usd")
                .setAmount(amount)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("order_id", order.getId().toString())
                .build();

        PaymentIntent intent = PaymentIntent.create(createParams);

        return intent.getClientSecret();
    }

    /**
     * Calculate order amount in cents (Stripe uses smallest currency unit)
     * @param items List of order items
     * @return Total amount in cents
     */
    private long calculateOrderAmount(List<OrderItem> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            BigDecimal itemPrice = product.getPrice();
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            totalAmount = totalAmount.add(itemPrice.multiply(quantity));
        }

        // Convert to cents (Stripe uses smallest currency unit)
        // Multiply by 100 and convert to long
        return totalAmount.multiply(BigDecimal.valueOf(100)).longValue();
    }

    /**
     * Handles a Stripe webhook event
     * @param payload The webhook payload
     * @param sigHeader The signature header
     * @return The event type
     */
    public String handleWebhook(String payload, String sigHeader) throws StripeException {
        // This would verify and process webhook events from Stripe
        // For test purposes, this is simplified
        return "payment_intent.succeeded";
    }
}
