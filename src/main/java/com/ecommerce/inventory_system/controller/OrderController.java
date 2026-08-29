package com.ecommerce.inventory_system.controller;

import com.ecommerce.inventory_system.dto.OrderRequest;
import com.ecommerce.inventory_system.dto.OrderResponse;
import com.ecommerce.inventory_system.entity.Order;
import com.ecommerce.inventory_system.entity.Product;
import com.ecommerce.inventory_system.entity.Seller;
import com.ecommerce.inventory_system.repository.ProductRepository;
import com.ecommerce.inventory_system.service.OrderService;
import com.ecommerce.inventory_system.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final SellerService sellerService;

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Enriched order details with product name, seller name, image
    @GetMapping("/customer/{customerId}/detailed")
    public ResponseEntity<List<Map<String, Object>>> getDetailedOrders(@PathVariable Integer customerId) {
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        List<Product> products = productRepository.findAll();
        List<Seller> sellers = sellerService.getAllSellers();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Order o : orders) {
            Product p = products.stream().filter(pr -> pr.getProductId().equals(o.getProductId())).findFirst().orElse(null);
            Seller s = sellers.stream().filter(sl -> sl.getSellerId().equals(o.getSellerId())).findFirst().orElse(null);

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("orderId", o.getOrderId());
            m.put("customerId", o.getCustomerId());
            m.put("productId", o.getProductId());
            m.put("productName", p != null ? p.getProductName() : "Product #" + o.getProductId());
            m.put("productCategory", p != null ? p.getCategory() : "");
            m.put("productDescription", p != null ? p.getDescription() : "");
            m.put("sellerId", o.getSellerId());
            m.put("sellerName", s != null ? s.getBusinessName() : "Seller #" + o.getSellerId());
            m.put("quantity", o.getQuantity());
            m.put("priceAtPurchase", o.getPriceAtPurchase());
            m.put("subtotal", o.getSubtotal());
            m.put("totalAmount", o.getTotalAmount());
            m.put("orderDate", o.getOrderDate());
            m.put("status", o.getStatus());
            m.put("paymentStatus", o.getPaymentStatus());
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }
}