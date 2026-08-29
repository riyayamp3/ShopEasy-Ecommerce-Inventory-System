package com.ecommerce.inventory_system.repository;

import com.ecommerce.inventory_system.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByCustomerId(Integer customerId);
    List<Complaint> findByStatus(String status);
    List<Complaint> findByOrderId(Integer orderId);
}