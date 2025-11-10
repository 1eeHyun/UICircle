package edu.uic.marketplace.controller.auth.docs;

import edu.uic.marketplace.dto.request.auth.LoginRequest;
import edu.uic.marketplace.dto.request.auth.SignupRequest;
import edu.uic.marketplace.dto.response.auth.LoginResponse;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;

public interface AuthApiDocs {

    ResponseEntity<CommonResponse<Void>> signUp(@RequestBody SignupRequest req);
    ResponseEntity<CommonResponse<LoginResponse>> logIn(@RequestBody LoginRequest req);

    ResponseEntity<CommonResponse<Void>> logout(@RequestBody String refreshToken);
}
