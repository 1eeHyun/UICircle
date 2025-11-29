package edu.uic.marketplace.controller.auth.docs;

import edu.uic.marketplace.dto.response.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(
        name = "Email Verifications",
        description = "Send or receive verification data"
)
public interface VerificationApiDocs {

    ResponseEntity<CommonResponse<Void>> verifyEmail(@RequestParam("token") String token);
}
