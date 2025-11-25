package edu.uic.marketplace.controller.notification.api;

import edu.uic.marketplace.controller.notification.docs.NotificationApiDocs;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.notification.NotificationResponse;
import edu.uic.marketplace.service.notification.NotificationService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationApiDocs {

    private final AuthValidator authValidator;
    private final NotificationService notificationService;

    @Override
    public ResponseEntity<CommonResponse<PageResponse<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String username = authValidator.extractUsername();
        PageResponse<NotificationResponse> res = notificationService.getUserNotifications(username, page, size);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<PageResponse<NotificationResponse>>> getUnreadNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String username = authValidator.extractUsername();
        PageResponse<NotificationResponse> res = notificationService.getUnreadNotifications(username, page, size);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<Long>> getUnreadCount() {

        String username = authValidator.extractUsername();
        Long res = notificationService.getUnreadCount(username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<NotificationResponse>> markAsRead(
            @PathVariable String notificationId) {

        String username = authValidator.extractUsername();
        NotificationResponse res = notificationService.markAsRead(notificationId, username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> markAllAsRead() {

        String username = authValidator.extractUsername();
        notificationService.markAllAsRead(username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> deleteNotification(
            @PathVariable String notificationId) {

        String username = authValidator.extractUsername();
        notificationService.deleteNotification(notificationId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> deleteAllNotifications() {

        String username = authValidator.extractUsername();
        notificationService.deleteAllNotifications(username);

        return ResponseEntity.ok(CommonResponse.success());
    }
}
