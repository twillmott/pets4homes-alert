package com.twillmott.pets4homesalert.service;

import com.twillmott.pets4homesalert.dto.Notification;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void sendNotification(Notification notification);
}
