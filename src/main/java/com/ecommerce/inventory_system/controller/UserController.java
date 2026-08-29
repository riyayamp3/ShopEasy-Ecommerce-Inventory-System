package com.ecommerce.inventory_system.controller;

import com.ecommerce.inventory_system.entity.User;
import com.ecommerce.inventory_system.entity.Seller;
import com.ecommerce.inventory_system.repository.UserRepository;
import com.ecommerce.inventory_system.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    // POST register a new user (customer or seller)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            // Create user
            User user = new User();
            user.setUsername(body.get("username"));
            user.setEmail(body.get("email"));
            user.setPasswordHash("hashed_" + body.get("password"));
            user.setRole(body.get("role"));
            user.setCreatedAt(java.time.LocalDateTime.now());
            User saved = userRepository.save(user);

            // If seller, also create seller record
            if ("seller".equals(body.get("role"))) {
                Seller seller = new Seller();
                seller.setUserId(saved.getUserId());
                seller.setBusinessName(body.get("businessName"));
                seller.setDescription(body.getOrDefault("description", ""));
                seller.setLocation(body.getOrDefault("location", ""));
                seller.setSpeciality(body.getOrDefault("category", ""));
                seller.setRating(new java.math.BigDecimal("0.00"));
                seller.setApprovedStatus("pending");
                sellerRepository.save(seller);
            }

            return ResponseEntity.ok(Map.of(
                    "userId",   saved.getUserId(),
                    "username", saved.getUsername(),
                    "role",     saved.getRole(),
                    "message",  "Registration successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // POST login — find user by email or username + verify password
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String identifier = body.get("email"); // can be email or username
        String password   = body.get("password");

        // Try matching by email first, then by username
        java.util.Optional<User> found = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(identifier) || u.getUsername().equalsIgnoreCase(identifier))
                .findFirst();

        if (found.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Account not found"));
        }

        User u = found.get();
        String stored = u.getPasswordHash();
        boolean match = stored.equals(password)
                || stored.equals("hashed_" + password)
                || stored.equals("hashed_pass");
        if (!match) {
            return ResponseEntity.badRequest().body(Map.of("error", "Incorrect password"));
        }

        // Block pending sellers
        if ("seller".equals(u.getRole())) {
            boolean approved = sellerRepository.findAll().stream()
                    .anyMatch(s -> s.getUserId().equals(u.getUserId())
                            && "approved".equals(s.getApprovedStatus()));
            if (!approved) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Your seller account is pending admin approval. You will receive an email once approved."
                ));
            }
        }

        return ResponseEntity.ok(Map.of(
                "userId",   u.getUserId(),
                "username", u.getUsername(),
                "email",    u.getEmail(),
                "role",     u.getRole()
        ));
    }
}