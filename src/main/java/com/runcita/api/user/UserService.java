package com.runcita.api.user;

import com.runcita.api.shared.models.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User service
 */
@Service
public class UserService {

    private UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieve a user by id
     * @param userId
     * @return user
     */
    public User getUserById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId, 2).orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Retrieve a user by email
     * @param email
     * @return user
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Save a user
     * @param user
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Delete a user
     * @param user
     */
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    /**
     * Indicates if the email of user already exists
     * @param email
     * @return boolean
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
