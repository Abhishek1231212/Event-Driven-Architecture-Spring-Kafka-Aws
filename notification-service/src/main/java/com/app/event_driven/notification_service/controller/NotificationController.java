package com.app.event_driven.notification_service.controller;


import com.app.event_driven.notification_service.service.NotificationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationListener notificationListener;

    @GetMapping
    public ResponseEntity<String> getNotifications(){
        notificationListener.pollSqsMessages();
        return ResponseEntity.ok("Notification updated");
    }
}
