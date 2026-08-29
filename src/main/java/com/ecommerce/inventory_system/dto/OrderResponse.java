package com.ecommerce.inventory_system.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Integer orderId;
    private Integer productId;
    private Integer sellerId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;
    private String status;
    private LocalDateTime orderDate;
}