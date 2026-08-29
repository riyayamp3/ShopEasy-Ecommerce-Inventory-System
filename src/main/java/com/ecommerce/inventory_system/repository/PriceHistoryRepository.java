package com.ecommerce.inventory_system.repository;

import com.ecommerce.inventory_system.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Integer> {

    // Get full price timeline for a product+seller, oldest first
    List<PriceHistory> findByProductIdAndSellerIdOrderByChangedAtAsc(Integer productId, Integer sellerId);
}