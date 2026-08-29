package com.ecommerce.inventory_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${shopeasy.mail.from:shopeasynotify@gmail.com}")
    private String fromEmail;

    @Async
    public void sendApprovalEmail(String to, String username, String businessName) {
        String html = """
            <!DOCTYPE html>
            <html>
            <body style="font-family:Segoe UI,sans-serif;background:#f7f7f5;padding:40px 0;margin:0">
              <div style="max-width:560px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08)">
                <div style="background:#0d0d0d;padding:32px 40px;text-align:center">
                  <div style="font-size:28px;font-weight:900;color:#fff;font-style:italic">Shop<span style="color:#f5c518">Easy</span></div>
                </div>
                <div style="padding:40px">
                  <h2 style="text-align:center;color:#111;margin-bottom:8px;font-size:22px">Store Approved!</h2>
                  <p style="text-align:center;color:#666;margin-bottom:24px">Hi %s, great news!</p>
                  <div style="background:#f0fdf4;border:1px solid #bbf7d0;border-radius:12px;padding:20px;margin-bottom:28px">
                    <p style="color:#166534;font-size:15px;margin:0;line-height:1.6">
                      Your store <strong>"%s"</strong> has been reviewed and <strong>approved</strong> by our admin team.
                      You can now log in to your seller dashboard and start listing products.
                    </p>
                  </div>
                  <div style="text-align:center">
                    <a href="http://localhost:8080/login.html" style="display:inline-block;background:#f5c518;color:#0d0d0d;text-decoration:none;font-size:15px;font-weight:800;padding:14px 36px;border-radius:8px">Go to Seller Dashboard</a>
                  </div>
                  <p style="text-align:center;color:#94a3b8;font-size:12px;margin-top:32px">ShopEasy · Your trusted marketplace</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(username, businessName);

        send(to, "Your ShopEasy Store is Approved!", html);
    }

    @Async
    public void sendRejectionEmail(String to, String username, String businessName) {
        String html = """
            <!DOCTYPE html>
            <html>
            <body style="font-family:Segoe UI,sans-serif;background:#f7f7f5;padding:40px 0;margin:0">
              <div style="max-width:560px;margin:0 auto;background:#fff;border-radius:16px;overflow:hidden">
                <div style="background:#0d0d0d;padding:32px 40px;text-align:center">
                  <div style="font-size:28px;font-weight:900;color:#fff;font-style:italic">Shop<span style="color:#f5c518">Easy</span></div>
                </div>
                <div style="padding:40px">
                  <h2 style="color:#111;margin-bottom:12px">Application Update</h2>
                  <p style="color:#666;line-height:1.7">Hi %s, your store application for <strong>"%s"</strong> requires additional review.
                  Please contact our support team for more information.</p>
                  <p style="text-align:center;color:#94a3b8;font-size:12px;margin-top:32px">ShopEasy · Your trusted marketplace</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(username, businessName);

        send(to, "Update on your ShopEasy Store Application", html);
    }

    private void send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
