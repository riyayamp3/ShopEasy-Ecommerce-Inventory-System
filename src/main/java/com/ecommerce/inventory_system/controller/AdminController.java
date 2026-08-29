package com.ecommerce.inventory_system.controller;

import com.ecommerce.inventory_system.entity.Seller;
import com.ecommerce.inventory_system.entity.User;
import com.ecommerce.inventory_system.entity.PriceHistory;
import com.ecommerce.inventory_system.repository.PriceHistoryRepository;
import com.ecommerce.inventory_system.repository.UserRepository;
import com.ecommerce.inventory_system.service.NotificationService;
import com.ecommerce.inventory_system.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final SellerService sellerService;
    private final PriceHistoryRepository priceHistoryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // ── Role guard helper ──
    private ResponseEntity<?> checkAdmin(String userId) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }
        try {
            Optional<User> userOpt = userRepository.findById(Integer.parseInt(userId));
            if (userOpt.isEmpty() || !"admin".equals(userOpt.get().getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid user ID"));
        }
        return null; // null = access granted
    }

    // GET all sellers
    @GetMapping("/sellers")
    public ResponseEntity<?> getAllSellers(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        ResponseEntity<?> guard = checkAdmin(userId);
        if (guard != null) return guard;

        List<Seller> sellers = sellerService.getAllSellers();
        List<User>   users   = userRepository.findAll();

        List<Map<String, Object>> result = sellers.stream().map(s -> {
            User u = users.stream()
                    .filter(user -> user.getUserId().equals(s.getUserId()))
                    .findFirst().orElse(null);
            return Map.<String, Object>of(
                    "sellerId",       s.getSellerId(),
                    "businessName",   s.getBusinessName(),
                    "rating",         s.getRating() != null ? s.getRating() : 0,
                    "approvedStatus", s.getApprovedStatus(),
                    "approvedBy",     s.getApprovedBy() != null ? s.getApprovedBy() : "",
                    "approvedAt",     s.getApprovedAt() != null ? s.getApprovedAt().toString() : "",
                    "userId",         s.getUserId(),
                    "username",       u != null ? u.getUsername() : "—",
                    "email",          u != null ? u.getEmail() : "—"
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // POST approve seller
    @PostMapping("/sellers/{sellerId}/approve")
    public ResponseEntity<?> approveSeller(
            @PathVariable Integer sellerId,
            @RequestParam Integer adminUserId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        ResponseEntity<?> guard = checkAdmin(userId);
        if (guard != null) return guard;

        Seller approved = sellerService.approveSeller(sellerId, adminUserId);

        // Find the seller's user account and send notification + email
        userRepository.findById(approved.getUserId()).ifPresent(u ->
            notificationService.notifySellerApproved(
                u.getUserId(),
                approved.getBusinessName(),
                u.getEmail(),
                u.getUsername()
            )
        );

        return ResponseEntity.ok(approved);
    }

    // GET all users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        ResponseEntity<?> guard = checkAdmin(userId);
        if (guard != null) return guard;
        return ResponseEntity.ok(userRepository.findAll());
    }

    // GET all orders (admin)
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        ResponseEntity<?> guard = checkAdmin(userId);
        if (guard != null) return guard;
        return ResponseEntity.ok(Map.of("message", "Use /api/orders/all with admin header"));
    }

    // GET price history
    @GetMapping("/price-history")
    public ResponseEntity<?> getPriceHistory(
            @RequestParam Integer productId,
            @RequestParam Integer sellerId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        ResponseEntity<?> guard = checkAdmin(userId);
        if (guard != null) return guard;
        return ResponseEntity.ok(
                priceHistoryRepository
                        .findByProductIdAndSellerIdOrderByChangedAtAsc(productId, sellerId));
    }

    // POST verify admin credentials — used by admin.html on load
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAdmin(@RequestBody Map<String, String> body) {
        String userIdStr = body.get("userId");
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No user ID provided"));
        }
        try {
            Optional<User> userOpt = userRepository.findById(Integer.parseInt(userIdStr));
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }
            User user = userOpt.get();
            if (!"admin".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. Admin role required.",
                                     "role", user.getRole()));
            }
            return ResponseEntity.ok(Map.of(
                    "verified", true,
                    "username", user.getUsername(),
                    "userId",   user.getUserId()
            ));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid user ID"));
        }
    }
}
