package com.ecommerce.inventory_system.service;

import com.ecommerce.inventory_system.entity.Notification;
import com.ecommerce.inventory_system.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public Notification createNotification(Integer userId, String title, String message, String type) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setIsRead(false);
        n.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(n);
    }

    public void notifySellerApproved(Integer userId, String businessName, String email, String username) {
        // In-app notification (synchronous — fast DB write)
        createNotification(
            userId,
            "Your store has been approved!",
            "Congratulations " + username + "! Your store \"" + businessName +
            "\" has been reviewed and approved. You can now log in and start selling.",
            "approval"
        );
        // Email (async — runs in background thread via EmailService)
        emailService.sendApprovalEmail(email, username, businessName);
    }

    public void notifySellerRejected(Integer userId, String businessName, String email, String username) {
        createNotification(
            userId,
            "Store application update",
            "Your store \"" + businessName + "\" application requires additional review. " +
            "Please contact our support team for more information.",
            "rejection"
        );
        emailService.sendRejectionEmail(email, username, businessName);
    }
}
