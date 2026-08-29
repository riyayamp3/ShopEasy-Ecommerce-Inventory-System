package com.ecommerce.inventory_system.repository;

import com.ecommerce.inventory_system.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    // Get all inventory rows for a product, cheapest first
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId AND i.currentQuantity >= :qty ORDER BY i.price ASC")
    List<Inventory> findAvailableSellers(@Param("productId") Integer productId,
                                         @Param("qty") Integer qty);
}