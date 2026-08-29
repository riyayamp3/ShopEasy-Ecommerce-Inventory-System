package com.ecommerce.inventory_system.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InventoryUpdateRequest {
    private Integer productId;
    private Integer sellerId;
    private Integer quantity;
    private BigDecimal price;
}