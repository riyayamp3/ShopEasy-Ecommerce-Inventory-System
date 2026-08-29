package com.ecommerce.inventory_system.service;

import com.ecommerce.inventory_system.dto.OrderRequest;
import com.ecommerce.inventory_system.dto.OrderResponse;
import com.ecommerce.inventory_system.entity.Inventory;
import com.ecommerce.inventory_system.entity.Order;
import com.ecommerce.inventory_system.entity.PriceHistory;
import com.ecommerce.inventory_system.repository.InventoryRepository;
import com.ecommerce.inventory_system.repository.OrderRepository;
import com.ecommerce.inventory_system.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    // ── Place a new order ──────────────────────────────────────
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {

        // 1. Find best available seller (lowest price, enough stock)
        List<Inventory> available = inventoryRepository
                .findAvailableSellers(request.getProductId(), request.getQuantity());

        if (available.isEmpty()) {
            throw new IllegalArgumentException(
                    "No seller has enough stock for product id: " + request.getProductId());
        }

        // 2. Pick the best seller (first = cheapest due to ORDER BY price ASC)
        Inventory chosen = available.get(0);

        // 3. Deduct stock — this is the concurrency-safe step
        chosen.setCurrentQuantity(chosen.getCurrentQuantity() - request.getQuantity());
        chosen.setLastUpdated(LocalDateTime.now());
        chosen.setVersion(chosen.getVersion() + 1);
        inventoryRepository.save(chosen);

        // 4. Calculate amounts
        BigDecimal price    = chosen.getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(request.getQuantity()));

        // 5. Create the order
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setSellerId(chosen.getSellerId());
        order.setInventoryId(chosen.getInventoryId());
        order.setQuantity(request.getQuantity());
        order.setPriceAtPurchase(price);
        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("processing");
        order.setPaymentStatus("pending");

        Order saved = orderRepository.save(order);

        // 6. Log price in history
        PriceHistory history = new PriceHistory();
        history.setProductId(request.getProductId());
        history.setSellerId(chosen.getSellerId());
        history.setPrice(price);
        history.setChangeReason("Order placed");
        history.setChangedAt(LocalDateTime.now());
        priceHistoryRepository.save(history);

        // 7. Build and return response
        OrderResponse response = new OrderResponse();
        response.setOrderId(saved.getOrderId());
        response.setProductId(saved.getProductId());
        response.setSellerId(saved.getSellerId());
        response.setQuantity(saved.getQuantity());
        response.setPriceAtPurchase(saved.getPriceAtPurchase());
        response.setSubtotal(saved.getSubtotal());
        response.setStatus(saved.getStatus());
        response.setOrderDate(saved.getOrderDate());

        return response;
    }

    // ── Cancel an order ────────────────────────────────────────
    @Transactional
    public String cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus().equals("cancelled")) {
            throw new IllegalArgumentException("Order is already cancelled");
        }
        if (order.getStatus().equals("delivered")) {
            throw new IllegalArgumentException("Cannot cancel a delivered order");
        }

        // Restore inventory
        Inventory inventory = inventoryRepository.findById(order.getInventoryId())
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

        inventory.setCurrentQuantity(inventory.getCurrentQuantity() + order.getQuantity());
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Update Order Status
        order.setStatus("cancelled");
        order.setPaymentStatus("refunded");
        orderRepository.save(order);

        return "Order " + orderId + " cancelled and inventory restored.";
    }

    // ── Get all orders for a customer ──────────────────────────
    public List<Order> getOrdersByCustomer(Integer customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    // ── Get all orders (admin) ─────────────────────────────────
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}