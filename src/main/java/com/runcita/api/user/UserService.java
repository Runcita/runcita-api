package com.runcita.api.user;

import com.runcita.api.shared.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User service
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    /**
     * Retrieve a user by id
     * @param userId
     * @return user
     */
    public User getUserById(Long userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        return optionalUser.get();
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
    public User save(User user) {
        return userRepository.save(user);
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
