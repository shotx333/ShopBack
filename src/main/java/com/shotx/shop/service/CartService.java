package com.shotx.shop.service;

import com.shotx.shop.model.Cart;
import com.shotx.shop.model.CartItem;
import com.shotx.shop.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Retrieves a cart for the specified username.
     * If no cart exists, one is created.
     */
    @Transactional
    public Cart getOrCreateCart(String username) {
        Cart cart = cartRepository.findByUsername(username);
        if (cart == null) {
            cart = new Cart(username);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    @Transactional
    public Cart addOrUpdateCartItem(String username, Long productId, int quantity) {
        Cart cart = getOrCreateCart(username);
        // Check if an item for the product already exists.
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(productId, quantity, cart);
            cart.getItems().add(newItem);
        }
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeCartItem(String username, Long productId) {
        Cart cart = getOrCreateCart(username);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateCartItem(String username, Long productId, int quantity) {
        Cart cart = getOrCreateCart(username);
        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        return cartRepository.save(cart);
    }
}
