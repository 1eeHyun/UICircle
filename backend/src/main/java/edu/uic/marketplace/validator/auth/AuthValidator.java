package edu.uic.marketplace.validator.auth;

import edu.uic.marketplace.exception.auth.UserNotAuthorizedException;
import edu.uic.marketplace.exception.auth.UserNotFoundException;
import edu.uic.marketplace.exception.auth.UserNotLoggedInException;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // =================================================================
    // Authentication Methods
    // =================================================================

    /**
     * Validate user login credentials
     * Returns the authenticated user if credentials are valid
     */
    public User validateLogin(String emailOrUsername, String password) {

        String input = (emailOrUsername == null) ? "" : emailOrUsername.trim();

        User user = userRepository.findByEmailOrUsernameAndStatus(input, UserStatus.ACTIVE)
                .orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UserNotFoundException("Invalid email/username or password");
        }

        return user;
    }

    // =================================================================
    // External API Validation - Use publicId
    // =================================================================

    /**
     * Validate and retrieve user by public ID (for external API calls)
     * Ensures user is active and not deleted
     */
    public User validateUserByPublicId(String publicId) {

        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + publicId + " not found"));

        if (!user.isActive())
            throw new UserNotAuthorizedException("User account is not active");

        return user;
    }

    /**
     * Validate user by public ID and ensure they have admin role
     */
    public User validateAdminByPublicId(String publicId) {

        User user = validateUserByPublicId(publicId);

        if (!user.getRole().equals(UserRole.ADMIN))
            throw new UserNotAuthorizedException("User does not have admin privileges");

        return user;
    }

    /**
     * Validate user by public ID and ensure they have professor role
     */
    public User validateProfessorByPublicId(String publicId) {

        User user = validateUserByPublicId(publicId);

        if (!user.getRole().equals(UserRole.PROFESSOR))
            throw new UserNotAuthorizedException("User does not have professor privileges");

        return user;
    }

    /**
     * Validate user by public ID with specific status
     */
    public User validateUserByPublicIdAndStatus(String publicId, UserStatus expectedStatus) {

        User user = userRepository.findByPublicIdAndStatus(publicId, expectedStatus)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with ID " + publicId + " and status " + expectedStatus + " not found"));

        return user;
    }

    // =================================================================
    // Authentication Context Methods
    // =================================================================

    /**
     * Validate user by username (for authentication)
     */
    public User validateUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));

        if (!user.isActive())
            throw new UserNotAuthorizedException("User account is not active");

        return user;
    }

    /**
     * Validate that user is logged in
     */
    public void validateLoggedIn() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UserNotLoggedInException("User is not logged in");
        }
    }

    /**
     * Extract username from user details
     */
    public String extractUsername() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UserNotLoggedInException("User is not logged in");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails ud) {
            return ud.getUsername();
        }
        if (principal instanceof String s && !"anonymousUser".equals(s)) {
            return s;
        }

        throw new UserNotLoggedInException("User is not logged in");
    }

    /**
     * Get logged-in user from user details
     */
    public User getLoggedInUser() {

        String username = extractUsername();
        return userRepository.findByUsernameIgnoreCase(username)
                .or(() -> userRepository.findByEmailIgnoreCase(username))
                .filter(User::isActive)
                .orElseThrow(() -> new UserNotAuthorizedException("User account is not active"));
    }

    // =================================================================
    // Authorization Methods
    // =================================================================

    /**
     * Validate that the current user owns the resource
     */
    public void validateOwnership(User currentUser, User resourceOwner) {

        if (!currentUser.getUserId().equals(resourceOwner.getUserId()))
            throw new UserNotAuthorizedException("User does not have permission to access this resource");
    }

    /**
     * Validate that the current user owns the resource or is an admin
     */
    public void validateOwnershipOrAdmin(User currentUser, User resourceOwner) {

        if (!currentUser.getUserId().equals(resourceOwner.getUserId())
                && !currentUser.getRole().equals(UserRole.ADMIN))
            throw new UserNotAuthorizedException("User does not have permission to access this resource");
    }

    /**
     * Validate that the user has admin role
     */
    public void validateAdminRole(User user) {

        if (!user.getRole().equals(UserRole.ADMIN))
            throw new UserNotAuthorizedException("User does not have admin privileges");
    }

    /**
     * Validate that the user has professor or admin role
     */
    public void validateProfessorOrAdminRole(User user) {

        if (!user.getRole().equals(UserRole.PROFESSOR) && !user.getRole().equals(UserRole.ADMIN))
            throw new UserNotAuthorizedException("User does not have sufficient privileges");
    }

    // =================================================================
    // Internal Methods - Use Long ID only for FK relationships
    // =================================================================

    /**
     * Validate user by internal ID (for internal FK operations only)
     * Do not expose this in external APIs
     */
    User validateUserByIdInternal(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with internal ID " + userId + " not found"));
    }

    /**
     * Validate admin by internal ID (for internal operations only)
     */
    User validateAdminByIdInternal(Long userId) {
        User user = validateUserByIdInternal(userId);
        validateAdminRole(user);
        return user;
    }
}
