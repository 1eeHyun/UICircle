package edu.uic.marketplace.controller.auth.api;

import edu.uic.marketplace.controller.auth.docs.VerificationApiDocs;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class VerificationController implements VerificationApiDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/verify-email")
    public ResponseEntity<CommonResponse<Void>> verifyEmail(@RequestParam("token") String token) {

        authService.verifyEmail(token);
        return ResponseEntity.ok(CommonResponse.success());
    }
}
