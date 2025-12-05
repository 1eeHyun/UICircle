package edu.uic.marketplace.controller.user.api;

import edu.uic.marketplace.controller.user.docs.ProfileApiDocs;
import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.service.user.ProfileService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController implements ProfileApiDocs {

    private final AuthValidator authValidator;
    private final ProfileService profileService;

    /**
     * Get public profile by public ID (for viewing other user's profiles)
     */
    @GetMapping("/public/{publicId}")
    public ResponseEntity<CommonResponse<ProfileResponse>> getPublicProfile(
            @PathVariable String publicId) {
        ProfileResponse res = profileService.getPublicProfile(publicId);
        return ResponseEntity.ok(CommonResponse.success(res));
    }

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

    /**
     * Upload avatar image file
     */
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<ProfileResponse>> uploadAvatarFile(
            @RequestParam("file") MultipartFile file) {
        String username = authValidator.extractUsername();
        ProfileResponse res = profileService.uploadAvatarFile(username, file);
        return ResponseEntity.ok(CommonResponse.success(res));
    }

    /**
     * Upload banner image file
     */
    @PostMapping(value = "/me/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<ProfileResponse>> uploadBannerFile(
            @RequestParam("file") MultipartFile file) {
        String username = authValidator.extractUsername();
        ProfileResponse res = profileService.uploadBannerFile(username, file);
        return ResponseEntity.ok(CommonResponse.success(res));
    }
}
