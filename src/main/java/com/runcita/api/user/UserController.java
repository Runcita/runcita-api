package com.runcita.api.user;

import com.runcita.api.config.security.jwt.JWTFilter;
import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.NewEmail;
import com.runcita.api.shared.models.NewPassword;
import com.runcita.api.shared.models.Profile;
import com.runcita.api.shared.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public UserController(PasswordEncoder passwordEncoder, UserService userService, TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Update user password
     * @param userId
     * @param newPassword
     * @return
     */
    @PutMapping(value = "/{userId}/updatepassword", consumes = { "application/json" })
    public ResponseEntity updatePassword(@PathVariable("userId") Long userId, @Valid @RequestBody NewPassword newPassword) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), newPassword.getOldPassword());
        try {
            authenticationManager.authenticate(authenticationToken);
            user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
            userService.saveUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Password is incorrect", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Update user email
     * @param userId
     * @param newEmail
     * @return
     */
    @PutMapping(value = "/{userId}/updateemail", consumes = { "application/json" })
    public ResponseEntity updateEmail(@PathVariable("userId") Long userId, @Valid @RequestBody NewEmail newEmail) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        if (userService.emailExists(newEmail.getNewEmail())) {
            return new ResponseEntity<>("Email already exist", HttpStatus.BAD_REQUEST);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), newEmail.getPassword());
        try {
            authenticationManager.authenticate(authenticationToken);
            user.setEmail(newEmail.getNewEmail());
            userService.saveUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Password is incorrect", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Delete user
     * @param request
     * @param userId
     */
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity deleteUser(HttpServletRequest request, @PathVariable("userId") Long userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        String requestEmail = tokenProvider.getUsername(JWTFilter.resolveToken(request));
        if(!user.getEmail().equals(requestEmail)) {
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
    public ResponseEntity<Profile> recoverUser(@PathVariable("userId") Long userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        return new ResponseEntity<>(user.getProfile(), HttpStatus.OK);
    }

    /**
     * Update a user
     * @param userId
     * @return user
     */
    @PutMapping(value = "/{userId}", consumes = { "application/json" })
    public ResponseEntity<Profile> updateUser(HttpServletRequest request, @PathVariable("userId") Long userId, @Valid @RequestBody Profile profileUpdate) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        String requestEmail = tokenProvider.getUsername(JWTFilter.resolveToken(request));
        if(!user.getEmail().equals(requestEmail)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        profileUpdate.setId(user.getProfile().getId());
        user.setProfile(profileUpdate);
        userService.saveUser(user);
        return new ResponseEntity<>(user.getProfile(), HttpStatus.OK);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
