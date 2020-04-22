package com.runcita.api.user;

import com.runcita.api.shared.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Auth service
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
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
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
     * Recover email of user
     * @param user
     * @return email
     */
    public String getEmailUser(User user) {
        return userRepository.findEmailUser(user.getId());
    }

    /**
     * Subscribe to a user
     * @param user
     * @param otherUser
     */
    public void subscribeUser(User user, User otherUser) {
        userRepository.subscribeUser(user.getId(), otherUser.getId());
    }

    /**
     * Indicates if the subscription of a user for other user exists
     * @param user
     * @param subscriber
     */
    public boolean subscriptionUserExists(User user, User subscriber) {
        return userRepository.subscriptionUserExists(user.getId(), subscriber.getId());
    }

    /**
     * Recover subscribtions of user
     * @param user
     * @return subscriptions user
     */
    public List<User> getSubscriptionsOfUser(User user) {
        return userRepository.findSubscriptionsOfUser(user.getId());
    }

    /**
     * Recover subscribers of user
     * @param user
     * @return subscribers user
     */
    public List<User> getSubscribersOfUser(User user) {
        return userRepository.findSubscribersOfUser(user.getId());
    }
}
