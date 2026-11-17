package com.skyray16.notificationservice.listener;

import com.skyray16.notificationservice.event.UserEvent;
import com.skyray16.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);
    private final EmailService emailService;

    public UserEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group-v3")
    public void handleUserEvent(UserEvent event) {
        log.info("Получено сообщение из Kafka: {}", event);
        
        String text;
        if ("CREATED".equals(event.operation())) {
            text = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        } else if ("DELETED".equals(event.operation())) {
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            return;
        }

        try {
            emailService.sendEmail(event.email(), "Уведомление", text);
            log.info("Email успешно отправлен на {}", event.email());
        } catch (Exception e) {
            log.error("Не удалось отправить email на {}. Причина: {}", event.email(), e.getMessage());
        }
    }
}