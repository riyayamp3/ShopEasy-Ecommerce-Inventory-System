package com.ecommerce.inventory_system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@RestController
@RequestMapping("/api/mail-test")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MailTestController {

    private final JavaMailSender mailSender;

    @Value("${shopeasy.mail.from:shopeasynotify@gmail.com}")
    private String fromEmail;

    @GetMapping("/send")
    public Map<String, String> sendTest(@RequestParam String to) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("ShopEasy Test Email");
            helper.setText("<h2>Test email from ShopEasy</h2><p>If you see this, email is working!</p>", true);
            mailSender.send(message);
            return Map.of("status", "SUCCESS", "message", "Email sent to " + to);
        } catch (Exception e) {
            return Map.of("status", "FAILED", "error", e.getMessage(), "cause",
                    e.getCause() != null ? e.getCause().getMessage() : "none");
        }
    }
}
