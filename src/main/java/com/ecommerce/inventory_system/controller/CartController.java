package com.ecommerce.inventory_system.controller;

import com.ecommerce.inventory_system.entity.CartItem;
import com.ecommerce.inventory_system.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;

    // GET all cart items for a user
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartItemRepository.findByUserId(userId));
    }

    // POST add or update item in cart
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> body) {
        try {
            Integer userId    = Integer.parseInt(body.get("userId").toString());
            Integer productId = Integer.parseInt(body.get("productId").toString());
            int delta         = body.containsKey("quantity")
                                ? Integer.parseInt(body.get("quantity").toString()) : 1;

            Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, productId);
            CartItem item;
            if (existing.isPresent()) {
                item = existing.get();
                item.setQuantity(item.getQuantity() + delta);
            } else {
                item = new CartItem();
                item.setUserId(userId);
                item.setProductId(productId);
                item.setProductName(body.get("productName").toString());
                item.setCategory(body.getOrDefault("category", "").toString());
                item.setBasePrice(new BigDecimal(body.get("basePrice").toString()));
                item.setQuantity(delta);
                item.setImageUrl(body.getOrDefault("imageUrl", "").toString());
                item.setAddedAt(LocalDateTime.now());
            }
            return ResponseEntity.ok(cartItemRepository.save(item));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT update quantity directly
    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody Map<String, Object> body) {
        try {
            Integer userId    = Integer.parseInt(body.get("userId").toString());
            Integer productId = Integer.parseInt(body.get("productId").toString());
            int quantity      = Integer.parseInt(body.get("quantity").toString());

            Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, productId);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            CartItem item = existing.get();
            if (quantity <= 0) {
                cartItemRepository.delete(item);
                return ResponseEntity.ok(Map.of("message", "Item removed"));
            }
            item.setQuantity(quantity);
            return ResponseEntity.ok(cartItemRepository.save(item));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE single item
    @Transactional
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<?> removeItem(@PathVariable Integer userId, @PathVariable Integer productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok(Map.of("message", "Removed"));
    }

    // DELETE entire cart (after checkout)
    @Transactional
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Integer userId) {
        cartItemRepository.deleteByUserId(userId);
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }

    // POST sync cart (merge localStorage cart into DB on login)
    @PostMapping("/sync")
    public ResponseEntity<?> syncCart(@RequestBody Map<String, Object> body) {
        try {
            Integer userId = Integer.parseInt(body.get("userId").toString());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

            for (Map<String, Object> itemData : items) {
                Integer productId = Integer.parseInt(itemData.get("productId").toString());
                int qty = Integer.parseInt(itemData.get("quantity").toString());

                Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, productId);
                if (existing.isPresent()) {
                    CartItem item = existing.get();
                    item.setQuantity(Math.max(item.getQuantity(), qty));
                    cartItemRepository.save(item);
                } else {
                    CartItem item = new CartItem();
                    item.setUserId(userId);
                    item.setProductId(productId);
                    item.setProductName(itemData.get("productName").toString());
                    item.setCategory(itemData.getOrDefault("category", "").toString());
                    item.setBasePrice(new BigDecimal(itemData.get("basePrice").toString()));
                    item.setQuantity(qty);
                    item.setImageUrl(itemData.getOrDefault("imageUrl", "").toString());
                    item.setAddedAt(LocalDateTime.now());
                    cartItemRepository.save(item);
                }
            }
            return ResponseEntity.ok(cartItemRepository.findByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
