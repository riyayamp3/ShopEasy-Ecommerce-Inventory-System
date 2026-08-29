package com.ecommerce.inventory_system.service;

import com.ecommerce.inventory_system.entity.Seller;
import com.ecommerce.inventory_system.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public Seller getSellerById(Integer id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with id: " + id));
    }

    public Seller approveSeller(Integer sellerId, Integer adminUserId) {
        Seller seller = getSellerById(sellerId);
        seller.setApprovedStatus("approved");
        seller.setApprovedBy(adminUserId);
        seller.setApprovedAt(java.time.LocalDateTime.now());
        return sellerRepository.save(seller);
    }
}