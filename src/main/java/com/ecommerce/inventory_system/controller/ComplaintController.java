package com.ecommerce.inventory_system.controller;

import com.ecommerce.inventory_system.entity.Complaint;
import com.ecommerce.inventory_system.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintRepository complaintRepository;

    // Customer: raise a complaint
    @PostMapping("/raise")
    public ResponseEntity<?> raiseComplaint(@RequestBody Map<String, String> body) {
        try {
            Complaint c = new Complaint();
            c.setCustomerId(Integer.parseInt(body.get("customerId")));
            String orderIdStr = body.get("orderId");
            c.setOrderId((orderIdStr != null && !orderIdStr.isBlank() && !orderIdStr.equals("0"))
                    ? Integer.parseInt(orderIdStr) : 0);
            c.setSubject(body.get("subject"));
            c.setDescription(body.get("description"));
            c.setImageUrl(body.getOrDefault("imageUrl", null));
            c.setStatus("open");
            c.setCreatedAt(LocalDateTime.now());
            return ResponseEntity.ok(complaintRepository.save(c));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Customer: view own complaints
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Complaint>> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(complaintRepository.findByCustomerId(customerId));
    }

    // Admin: view all complaints
    @GetMapping("/all")
    public ResponseEntity<List<Complaint>> getAll() {
        return ResponseEntity.ok(complaintRepository.findAll());
    }

    // Admin: view by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Complaint>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(complaintRepository.findByStatus(status));
    }

    // Admin: resolve a complaint
    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        return complaintRepository.findById(id).map(c -> {
            c.setStatus("resolved");
            c.setAdminNotes(body.get("adminNotes"));
            c.setResolvedAt(LocalDateTime.now());
            return ResponseEntity.ok(complaintRepository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Admin: update status (open → in_progress → resolved)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        return complaintRepository.findById(id).map(c -> {
            c.setStatus(body.get("status"));
            if ("resolved".equals(body.get("status"))) {
                c.setResolvedAt(LocalDateTime.now());
                c.setAdminNotes(body.getOrDefault("adminNotes", c.getAdminNotes()));
            }
            return ResponseEntity.ok(complaintRepository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
}