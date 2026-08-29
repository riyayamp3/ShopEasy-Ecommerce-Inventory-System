package com.ecommerce.inventory_system.controller;

import com.ecommerce.inventory_system.entity.*;
import com.ecommerce.inventory_system.repository.*;
import com.ecommerce.inventory_system.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seller-dashboard")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SellerDashboardController {

    private final SellerService sellerService;
    private final SellerRepository sellerRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    // GET seller profile by userId
    @GetMapping("/profile/by-user/{userId}")
    public ResponseEntity<?> getProfileByUser(@PathVariable Integer userId) {
        return sellerRepository.findAll().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT update seller profile
    @PutMapping("/profile/{sellerId}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer sellerId,
                                            @RequestBody Map<String, String> body) {
        try {
            Seller seller = sellerService.getSellerById(sellerId);
            if (body.containsKey("businessName") && !body.get("businessName").isBlank())
                seller.setBusinessName(body.get("businessName"));
            if (body.containsKey("description"))
                seller.setDescription(body.get("description"));
            if (body.containsKey("location"))
                seller.setLocation(body.get("location"));
            if (body.containsKey("speciality"))
                seller.setSpeciality(body.get("speciality"));
            return ResponseEntity.ok(sellerRepository.save(seller));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET enriched inventory for seller (with product details)
    @GetMapping("/inventory/{sellerId}")
    public ResponseEntity<List<Map<String, Object>>> getInventory(@PathVariable Integer sellerId) {
        List<Inventory> inventory = inventoryRepository.findAll().stream()
                .filter(i -> i.getSellerId().equals(sellerId))
                .collect(Collectors.toList());
        List<Product> products = productRepository.findAll();

        List<Map<String, Object>> result = inventory.stream().map(inv -> {
            Product p = products.stream()
                    .filter(pr -> pr.getProductId().equals(inv.getProductId()))
                    .findFirst().orElse(null);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("inventoryId",      inv.getInventoryId());
            m.put("productId",        inv.getProductId());
            m.put("productName",      p != null ? p.getProductName() : "Product #" + inv.getProductId());
            m.put("category",         p != null ? p.getCategory() : "");
            m.put("description",      p != null ? p.getDescription() : "");
            m.put("basePrice",        p != null ? p.getBasePrice() : 0);
            m.put("sellerPrice",      inv.getPrice());
            m.put("originalQuantity", inv.getOriginalQuantity());
            m.put("currentQuantity",  inv.getCurrentQuantity());
            m.put("sold",             inv.getOriginalQuantity() - inv.getCurrentQuantity());
            m.put("lastUpdated",      inv.getLastUpdated() != null ? inv.getLastUpdated().toString() : "");
            m.put("imageUrl",         inv.getImageUrl() != null ? inv.getImageUrl() : "");
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // GET enriched orders for seller
    @GetMapping("/orders/{sellerId}")
    public ResponseEntity<List<Map<String, Object>>> getOrders(@PathVariable Integer sellerId) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getSellerId().equals(sellerId))
                .collect(Collectors.toList());
        List<Product> products = productRepository.findAll();
        List<User> users = userRepository.findAll();

        List<Map<String, Object>> result = orders.stream().map(o -> {
            Product p = products.stream().filter(pr -> pr.getProductId().equals(o.getProductId())).findFirst().orElse(null);
            User u = users.stream().filter(us -> us.getUserId().equals(o.getCustomerId())).findFirst().orElse(null);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("orderId",         o.getOrderId());
            m.put("customerId",      o.getCustomerId());
            m.put("customerName",    u != null ? u.getUsername() : "Customer #" + o.getCustomerId());
            m.put("productId",       o.getProductId());
            m.put("productName",     p != null ? p.getProductName() : "Product #" + o.getProductId());
            m.put("productCategory", p != null ? p.getCategory() : "");
            m.put("quantity",        o.getQuantity());
            m.put("priceAtPurchase", o.getPriceAtPurchase());
            m.put("totalAmount",     o.getTotalAmount());
            m.put("status",          o.getStatus());
            m.put("paymentStatus",   o.getPaymentStatus());
            m.put("orderDate",       o.getOrderDate() != null ? o.getOrderDate().toString() : "");
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // GET complaints related to this seller's orders
    @GetMapping("/complaints/{sellerId}")
    public ResponseEntity<List<Map<String, Object>>> getComplaints(@PathVariable Integer sellerId) {
        // Get all order IDs for this seller
        Set<Integer> sellerOrderIds = orderRepository.findAll().stream()
                .filter(o -> o.getSellerId().equals(sellerId))
                .map(Order::getOrderId)
                .collect(Collectors.toSet());

        // Get complaints for those orders
        List<Complaint> complaints = complaintRepository.findAll().stream()
                .filter(c -> c.getOrderId() != null && sellerOrderIds.contains(c.getOrderId()))
                .collect(Collectors.toList());

        List<User> users = userRepository.findAll();

        List<Map<String, Object>> result = complaints.stream().map(c -> {
            User u = users.stream().filter(us -> us.getUserId().equals(c.getCustomerId())).findFirst().orElse(null);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("complaintId",   c.getComplaintId());
            m.put("customerId",    c.getCustomerId());
            m.put("customerName",  u != null ? u.getUsername() : "Customer #" + c.getCustomerId());
            m.put("orderId",       c.getOrderId());
            m.put("subject",       c.getSubject());
            m.put("description",   c.getDescription());
            m.put("status",        c.getStatus());
            m.put("adminNotes",    c.getAdminNotes());
            m.put("imageUrl",      c.getImageUrl());
            m.put("createdAt",     c.getCreatedAt() != null ? c.getCreatedAt().toString() : "");
            m.put("resolvedAt",    c.getResolvedAt() != null ? c.getResolvedAt().toString() : "");
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // GET price history by product name + seller
    @GetMapping("/price-history/{sellerId}")
    public ResponseEntity<List<Map<String, Object>>> getPriceHistory(
            @PathVariable Integer sellerId,
            @RequestParam(required = false) String productName) {

        List<Product> products = productRepository.findAll();
        List<Product> matched = products.stream()
                .filter(p -> productName == null || productName.isBlank()
                        || p.getProductName().toLowerCase().contains(productName.toLowerCase()))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Product p : matched) {
            List<PriceHistory> history = priceHistoryRepository
                    .findByProductIdAndSellerIdOrderByChangedAtAsc(p.getProductId(), sellerId);
            for (PriceHistory h : history) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("productId",    p.getProductId());
                m.put("productName",  p.getProductName());
                m.put("category",     p.getCategory());
                m.put("price",        h.getPrice());
                m.put("changeReason", h.getChangeReason());
                m.put("changedAt",    h.getChangedAt() != null ? h.getChangedAt().toString() : "");
                result.add(m);
            }
        }
        return ResponseEntity.ok(result);
    }

    // POST recalculate seller rating based on resolved complaints
    @PostMapping("/recalculate-rating/{sellerId}")
    public ResponseEntity<?> recalculateRating(@PathVariable Integer sellerId) {
        try {
            Set<Integer> sellerOrderIds = orderRepository.findAll().stream()
                    .filter(o -> o.getSellerId().equals(sellerId))
                    .map(Order::getOrderId)
                    .collect(Collectors.toSet());

            long totalComplaints = complaintRepository.findAll().stream()
                    .filter(c -> c.getOrderId() != null && sellerOrderIds.contains(c.getOrderId()))
                    .count();
            long resolvedComplaints = complaintRepository.findAll().stream()
                    .filter(c -> c.getOrderId() != null && sellerOrderIds.contains(c.getOrderId())
                            && "resolved".equals(c.getStatus()))
                    .count();

            // Rating formula: start at 5.0, deduct 0.2 per unresolved complaint, min 1.0
            long unresolved = totalComplaints - resolvedComplaints;
            double rating = Math.max(1.0, 5.0 - (unresolved * 0.2));

            Seller seller = sellerService.getSellerById(sellerId);
            seller.setRating(BigDecimal.valueOf(rating).setScale(2, RoundingMode.HALF_UP));
            sellerRepository.save(seller);

            return ResponseEntity.ok(Map.of("sellerId", sellerId, "newRating", rating,
                    "totalComplaints", totalComplaints, "unresolved", unresolved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST list a new product (add to inventory)
    @PostMapping("/list-product/{sellerId}")
    public ResponseEntity<?> listProduct(@PathVariable Integer sellerId,
                                          @RequestBody Map<String, Object> body) {
        try {
            Integer productId = Integer.parseInt(body.get("productId").toString());
            Integer quantity  = Integer.parseInt(body.get("quantity").toString());
            BigDecimal price  = new BigDecimal(body.get("price").toString());

            boolean exists = inventoryRepository.findAll().stream()
                    .anyMatch(i -> i.getProductId().equals(productId) && i.getSellerId().equals(sellerId));
            if (exists) return ResponseEntity.badRequest()
                    .body(Map.of("error", "You already list this product. Use Update Stock instead."));

            Inventory inv = new Inventory();
            inv.setProductId(productId);
            inv.setSellerId(sellerId);
            inv.setOriginalQuantity(quantity);
            inv.setCurrentQuantity(quantity);
            inv.setPrice(price);
            inv.setLastUpdated(java.time.LocalDateTime.now());
            inv.setVersion(1);
            if (body.containsKey("imageUrl") && body.get("imageUrl") != null) {
                inv.setImageUrl(body.get("imageUrl").toString());
            }
            Inventory saved = inventoryRepository.save(inv);

            PriceHistory ph = new PriceHistory();
            ph.setProductId(productId);
            ph.setSellerId(sellerId);
            ph.setPrice(price);
            ph.setChangeReason("Initial listing by seller");
            ph.setChangedAt(java.time.LocalDateTime.now());
            priceHistoryRepository.save(ph);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
