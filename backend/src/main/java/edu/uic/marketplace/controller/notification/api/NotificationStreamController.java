package edu.uic.marketplace.controller.notification.api;

import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.security.JwtTokenProvider;
import edu.uic.marketplace.service.notification.NotificationSseService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationStreamController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthValidator authValidator;
    private final NotificationSseService notificationSseService;

    @GetMapping("/api/notifications/stream")
    public SseEmitter stream(@RequestParam("token") String token) {

        String username = jwtTokenProvider.getUsernameFromJWT(token);
        User user = authValidator.validateUserByUsername(username);

        return notificationSseService.createEmitter(user.getUserId());
    }
}