package com.shotx.shop.controller;

import com.shotx.shop.model.Cart;
import com.shotx.shop.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get current user's cart
    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        String username = authentication.getName();
        Cart cart = cartService.getOrCreateCart(username);
        return ResponseEntity.ok(cart);
    }

    // Add or update a cart item.
    @PostMapping("/item")
    public ResponseEntity<?> addItemToCart(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        if (quantity < 1) {
            return ResponseEntity
                    .badRequest()
                    .body("Quantity must be at least 1");
        }

        String username = authentication.getName();
        try {
            Cart cart = cartService.addOrUpdateCartItem(username, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Remove an item from the cart
    @DeleteMapping("/item")
    public ResponseEntity<Cart> removeItemFromCart(
            Authentication authentication,
            @RequestParam Long productId) {
        String username = authentication.getName();
        Cart cart = cartService.removeCartItem(username, productId);
        return ResponseEntity.ok(cart);
    }

    // Optionally, add an endpoint to update quantity
    @PutMapping("/item")
    public ResponseEntity<?> updateCartItem(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        if (quantity < 1) {
            return ResponseEntity
                    .badRequest()
                    .body("Quantity must be at least 1");
        }

        String username = authentication.getName();
        try {
            Cart cart = cartService.updateCartItem(username, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}