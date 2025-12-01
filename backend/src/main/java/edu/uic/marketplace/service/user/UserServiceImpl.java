package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String Username) {
        return Optional.empty();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public void suspendUser(Long userId, String reason) {

    }

    @Override
    public void activateUser(Long userId) {

    }
}
