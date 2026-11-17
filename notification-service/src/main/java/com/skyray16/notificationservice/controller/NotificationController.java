package com.skyray16.notificationservice.controller;

import com.skyray16.notificationservice.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public String sendEmail(@RequestBody EmailRequest request) {
        emailService.sendEmail(request.to(), request.subject(), request.text());
        return "Email отправлен (или попытка отправки совершена)";
    }

    public record EmailRequest(String to, String subject, String text) {}
}