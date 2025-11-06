package edu.uic.marketplace.validator.auth;

import edu.uic.marketplace.exception.auth.UserNotAuthorizedException;
import edu.uic.marketplace.exception.auth.UserNotFoundException;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User validateLogin(String userEmail, String password) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            throw new UserNotFoundException();

        return user;
    }

    // TODO: Later feature for logging-in
    public User validateUserById(Long id) {

        // TODO: check user status (ACTIVE, SUSPENDED, DELETED)
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

//        return null;
    }

    public User validateAdminById(Long id) {

        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            throw new UserNotFoundException();

        User user = found.get();
        if (!user.getRole().equals(UserRole.ADMIN))
            throw new UserNotAuthorizedException();

        return user;
    }
}
