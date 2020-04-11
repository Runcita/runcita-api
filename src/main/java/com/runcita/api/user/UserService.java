package com.runcita.api.user;

import com.runcita.api.shared.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    /**
     * Retrieve a user by email
     * @param email
     * @return user
     */
    public User getUserByEmail(String email) {
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
