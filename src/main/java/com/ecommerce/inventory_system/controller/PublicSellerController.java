package com.ecommerce.inventory_system.controller;

import com.ecommerce.inventory_system.entity.Inventory;
import com.ecommerce.inventory_system.entity.Product;
import com.ecommerce.inventory_system.entity.Seller;
import com.ecommerce.inventory_system.entity.User;
import com.ecommerce.inventory_system.repository.InventoryRepository;
import com.ecommerce.inventory_system.repository.ProductRepository;
import com.ecommerce.inventory_system.repository.UserRepository;
import com.ecommerce.inventory_system.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sellers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PublicSellerController {

    private final SellerService sellerService;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;

    // GET all approved sellers (public listing)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllApprovedSellers() {
        List<Seller> sellers = sellerService.getAllSellers().stream()
                .filter(s -> "approved".equals(s.getApprovedStatus()))
                .collect(Collectors.toList());

        List<User> users = userRepository.findAll();

        List<Map<String, Object>> result = sellers.stream().map(s -> {
            User u = users.stream()
                    .filter(user -> user.getUserId().equals(s.getUserId()))
                    .findFirst().orElse(null);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("sellerId",     s.getSellerId());
            m.put("businessName", s.getBusinessName());
            m.put("rating",       s.getRating() != null ? s.getRating() : 0);
            m.put("username",     u != null ? u.getUsername() : "—");
            m.put("memberSince",  u != null && u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            m.put("description",  s.getDescription() != null ? s.getDescription() : "");
            m.put("location",     s.getLocation() != null ? s.getLocation() : "");
            m.put("speciality",   s.getSpeciality() != null ? s.getSpeciality() : "");
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // GET single seller profile with their products
    @GetMapping("/{sellerId}")
    public ResponseEntity<?> getSellerProfile(@PathVariable Integer sellerId) {
        Optional<Seller> sellerOpt = sellerService.getAllSellers().stream()
                .filter(s -> s.getSellerId().equals(sellerId))
                .findFirst();

        if (sellerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Seller seller = sellerOpt.get();
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUserId().equals(seller.getUserId()))
                .findFirst().orElse(null);

        // Get all inventory for this seller
        List<Inventory> inventory = inventoryRepository.findAll().stream()
                .filter(i -> i.getSellerId().equals(sellerId))
                .collect(Collectors.toList());

        // Enrich with product details
        List<Product> allProducts = productRepository.findAll();
        List<Map<String, Object>> listings = inventory.stream().map(inv -> {
            Product p = allProducts.stream()
                    .filter(prod -> prod.getProductId().equals(inv.getProductId()))
                    .findFirst().orElse(null);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("inventoryId",      inv.getInventoryId());
            m.put("productId",        inv.getProductId());
            m.put("productName",      p != null ? p.getProductName() : "Unknown");
            m.put("category",         p != null ? p.getCategory() : "");
            m.put("description",      p != null ? p.getDescription() : "");
            m.put("basePrice",        p != null ? p.getBasePrice() : 0);
            m.put("sellerPrice",      inv.getPrice());
            m.put("currentQuantity",  inv.getCurrentQuantity());
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("sellerId",     seller.getSellerId());
        profile.put("businessName", seller.getBusinessName());
        profile.put("rating",       seller.getRating() != null ? seller.getRating() : 0);
        profile.put("username",     user != null ? user.getUsername() : "—");
        profile.put("memberSince",  user != null && user.getCreatedAt() != null ? user.getCreatedAt().toString() : "");
        profile.put("description",  seller.getDescription() != null ? seller.getDescription() : "");
        profile.put("location",     seller.getLocation() != null ? seller.getLocation() : "");
        profile.put("speciality",   seller.getSpeciality() != null ? seller.getSpeciality() : "");
        profile.put("totalListings", listings.size());
        profile.put("listings",     listings);

        // Load reviews if table exists
        try {
            List<Map<String, Object>> reviews = jdbcTemplate.queryForList(
                "SELECT customer_name, rating, review_text, created_at FROM SELLER_REVIEW WHERE seller_id = ? ORDER BY created_at DESC",
                sellerId);
            profile.put("reviews", reviews);
        } catch (Exception e) {
            profile.put("reviews", List.of());
        }

        return ResponseEntity.ok(profile);
    }
}
