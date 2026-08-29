package com.ecommerce.inventory_system.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Integer customerId;
    private Integer productId;
    private Integer quantity;
}