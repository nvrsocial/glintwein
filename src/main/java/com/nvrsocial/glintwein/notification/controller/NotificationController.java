package com.nvrsocial.glintwein.notification.controller;

import com.nvrsocial.glintwein.notification.dto.NotificationResponse;
import com.nvrsocial.glintwein.notification.dto.UnreadCountResponse;
import com.nvrsocial.glintwein.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "false") boolean unreadOnly, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(notificationService.getNotifications(userId, unreadOnly, page, size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        notificationService.markAsRead(notificationId, userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        notificationService.markAllAsRead(userId);

        return ResponseEntity.noContent().build();
    }
}
