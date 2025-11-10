package edu.uic.marketplace.controller.auth.api;

import edu.uic.marketplace.controller.auth.docs.AuthApiDocs;
import edu.uic.marketplace.dto.request.auth.LoginRequest;
import edu.uic.marketplace.dto.request.auth.SignupRequest;
import edu.uic.marketplace.dto.response.auth.LoginResponse;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.service.auth.AuthService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final AuthService authService;
    private final AuthValidator authValidator;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Void>> signUp(@RequestBody SignupRequest req) {

        authService.signup(req);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> logIn(@RequestBody LoginRequest req) {

        LoginResponse res = authService.login(req);
        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> logout(@RequestBody String refreshToken) {

        // TODO: implement to expire session
        return ResponseEntity.ok(CommonResponse.success());
    }
}
