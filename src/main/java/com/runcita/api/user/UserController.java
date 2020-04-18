package com.runcita.api.user;

import com.runcita.api.config.security.jwt.JWTFilter;
import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * User controller
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final TokenProvider tokenProvider;

    public UserController(UserService userService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Delete user
     * @param request
     * @param userId
     */
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity deleteUser(HttpServletRequest request, @PathVariable("userId") Long userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        String emailRequest = tokenProvider.getUsername(JWTFilter.resolveToken(request));
        String emailUser = userService.getEmailUser(user);
        if(!emailRequest.equals(emailUser)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        userService.deleteUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Recover a user
     * @param userId
     * @return user
     */
    @GetMapping(value = "/{userId}")
    public ResponseEntity<User> recoverUser(@PathVariable("userId") Long userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Update a user
     * @param userId
     * @return user
     */
    @PutMapping(value = "/{userId}", consumes = { "application/json" })
    public ResponseEntity<User> updateUser(HttpServletRequest request, @PathVariable("userId") Long userId, @Valid @RequestBody User userUpdate) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        String emailRequest = tokenProvider.getUsername(JWTFilter.resolveToken(request));
        String emailUser = userService.getEmailUser(user);
        if(!emailRequest.equals(emailUser)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        userUpdate.setId(user.getId());
        userService.saveUser(userUpdate);
        return new ResponseEntity<>(userUpdate, HttpStatus.OK);
    }

//    /**
//     * Follow a user
//     * @param userId
//     * @return user
//     */
//    @PostMapping(value = "/{userId}/subscriptions/{otherUserId}")
//    public ResponseEntity FollowUser(HttpServletRequest request, @PathVariable("userId") Long userId, @PathVariable("otherUserId") Long otherUserId) throws UserNotFoundException {
//        User user = userService.getUserById(userId);
//        User otherUser = userService.getUserById(otherUserId);
//
//        String emailRequest = tokenProvider.getUsername(JWTFilter.resolveToken(request));
//        String emailUser = userService.getEmailUser(user);
//        if(!emailRequest.equals(emailUser)) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//
//        if(userService.subscriptionUserExists(user, otherUser)) {
//            return new ResponseEntity<>("Subscription already exist", HttpStatus.BAD_REQUEST);
//        }
//
//        userService.subscribeUser(user, otherUser);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }

    // recuperer les soubscriptions
    // GET /{userId}/subscriptions

    // recuperer les subcribers
    // GET /{userId}/subcribers

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
