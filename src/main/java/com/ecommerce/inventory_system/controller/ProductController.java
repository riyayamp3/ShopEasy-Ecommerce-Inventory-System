package com.ecommerce.inventory_system.controller;

import com.ecommerce.inventory_system.entity.Product;
import com.ecommerce.inventory_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // POST add a new product (admin only)
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, String> body) {
        try {
            Product p = new Product();
            p.setProductName(body.get("productName"));
            p.setDescription(body.get("description"));
            p.setCategory(body.get("category"));
            p.setBasePrice(new BigDecimal(body.get("basePrice")));
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            Product saved = productService.save(p);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}