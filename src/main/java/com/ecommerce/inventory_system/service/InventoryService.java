package com.ecommerce.inventory_system.service;

import com.ecommerce.inventory_system.dto.InventoryUpdateRequest;
import com.ecommerce.inventory_system.entity.Inventory;
import com.ecommerce.inventory_system.entity.PriceHistory;
import com.ecommerce.inventory_system.repository.InventoryRepository;
import com.ecommerce.inventory_system.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getAvailableSellers(Integer productId, Integer quantity) {
        return inventoryRepository.findAvailableSellers(productId, quantity);
    }

    @Transactional
    public Inventory updateInventory(InventoryUpdateRequest request) {
        // Find the inventory row for this product+seller
        Inventory inventory = inventoryRepository.findAll().stream()
                .filter(i -> i.getProductId().equals(request.getProductId())
                        && i.getSellerId().equals(request.getSellerId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

        // If price changed, log it in price history
        if (!inventory.getPrice().equals(request.getPrice())) {
            PriceHistory history = new PriceHistory();
            history.setProductId(request.getProductId());
            history.setSellerId(request.getSellerId());
            history.setPrice(request.getPrice());
            history.setChangeReason("Manual price update");
            history.setChangedAt(LocalDateTime.now());
            priceHistoryRepository.save(history);
        }

        // Update stock and price
        inventory.setCurrentQuantity(request.getQuantity());
        inventory.setPrice(request.getPrice());
        inventory.setLastUpdated(LocalDateTime.now());
        inventory.setVersion(inventory.getVersion() + 1);

        return inventoryRepository.save(inventory);
    }
}