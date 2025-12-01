package edu.uic.marketplace.controller.user.api;

import edu.uic.marketplace.controller.user.docs.ProfileApiDocs;
import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.service.user.ProfileService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController implements ProfileApiDocs {

    private final AuthValidator authValidator;
    private final ProfileService profileService;

    @Override
    @GetMapping("/{username}")
    public ResponseEntity<CommonResponse<ProfileResponse>> getProfileByUsername(
            @PathVariable String username) {

        authValidator.extractUsername();
        ProfileResponse res = profileService.getProfileByUsername(username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<ProfileResponse>> getMyProfile() {

        String username = authValidator.extractUsername();
        ProfileResponse res = profileService.getProfileByUsername(username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PutMapping("/me")
    public ResponseEntity<CommonResponse<ProfileResponse>> updateMyProfile(
            @RequestBody UpdateProfileRequest request) {

        String username = authValidator.extractUsername();
        ProfileResponse res = profileService.updateProfile(username, request);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PutMapping("/me/avatar")
    public ResponseEntity<CommonResponse<ProfileResponse>> updateAvatar(
            @RequestBody String imageUrl) {

        String username = authValidator.extractUsername();
        ProfileResponse res = profileService.uploadAvatar(username, imageUrl);
        return ResponseEntity.ok(CommonResponse.success(res));
    }
}
