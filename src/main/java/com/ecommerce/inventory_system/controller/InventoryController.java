package com.ecommerce.inventory_system.controller;

import com.ecommerce.inventory_system.dto.InventoryUpdateRequest;
import com.ecommerce.inventory_system.entity.Inventory;
import com.ecommerce.inventory_system.entity.PriceHistory;
import com.ecommerce.inventory_system.repository.InventoryRepository;
import com.ecommerce.inventory_system.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryRepository inventoryRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    // GET all inventory
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryRepository.findAll());
    }

    // GET available sellers for a product (multi-seller support)
    @GetMapping("/available")
    public ResponseEntity<List<Inventory>> getAvailableSellers(
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryRepository.findAvailableSellers(productId, quantity));
    }

    // GET all inventory for a specific seller
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Inventory>> getBySellerID(@PathVariable Integer sellerId) {
        List<Inventory> result = inventoryRepository.findAll().stream()
                .filter(i -> i.getSellerId().equals(sellerId))
                .toList();
        return ResponseEntity.ok(result);
    }

    // PUT update inventory
    @PutMapping("/update")
    public ResponseEntity<?> updateInventory(@RequestBody InventoryUpdateRequest request) {
        try {
            Inventory inventory = inventoryRepository.findAll().stream()
                    .filter(i -> i.getProductId().equals(request.getProductId())
                            && i.getSellerId().equals(request.getSellerId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

            if (!inventory.getPrice().equals(request.getPrice())) {
                PriceHistory history = new PriceHistory();
                history.setProductId(request.getProductId());
                history.setSellerId(request.getSellerId());
                history.setPrice(request.getPrice());
                history.setChangeReason("Manual price update by seller");
                history.setChangedAt(LocalDateTime.now());
                priceHistoryRepository.save(history);
            }

            inventory.setCurrentQuantity(request.getQuantity());
            inventory.setPrice(request.getPrice());
            inventory.setLastUpdated(LocalDateTime.now());
            inventory.setVersion(inventory.getVersion() + 1);
            return ResponseEntity.ok(inventoryRepository.save(inventory));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST seller adds a new product listing (existing product, new seller)
    @PostMapping("/list")
    public ResponseEntity<?> listProduct(@RequestBody Map<String, String> body) {
        try {
            Integer productId = Integer.parseInt(body.get("productId"));
            Integer sellerId  = Integer.parseInt(body.get("sellerId"));

            // Check if this seller already lists this product
            boolean exists = inventoryRepository.findAll().stream()
                    .anyMatch(i -> i.getProductId().equals(productId)
                            && i.getSellerId().equals(sellerId));
            if (exists) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "You already list this product. Update stock instead."));
            }

            Inventory inv = new Inventory();
            inv.setProductId(productId);
            inv.setSellerId(sellerId);
            inv.setOriginalQuantity(Integer.parseInt(body.get("quantity")));
            inv.setCurrentQuantity(Integer.parseInt(body.get("quantity")));
            inv.setPrice(new BigDecimal(body.get("price")));
            inv.setLastUpdated(LocalDateTime.now());
            inv.setVersion(1);

            Inventory saved = inventoryRepository.save(inv);

            // Log initial price
            PriceHistory history = new PriceHistory();
            history.setProductId(productId);
            history.setSellerId(sellerId);
            history.setPrice(saved.getPrice());
            history.setChangeReason("Initial listing by seller");
            history.setChangedAt(LocalDateTime.now());
            priceHistoryRepository.save(history);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}