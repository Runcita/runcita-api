package com.runcita.api.auth;

import com.runcita.api.config.security.jwt.JWTFilter;
import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.Auth;
import com.runcita.api.shared.models.NewEmail;
import com.runcita.api.shared.models.NewPassword;
import com.runcita.api.shared.models.User;
import com.runcita.api.user.UserService;
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
import java.util.Optional;

/**
 * Auth controller
 */
@Slf4j
@RestController
@CrossOrigin
public class AuthController {

    private final UserService userService;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthController(PasswordEncoder passwordEncoder, UserService userService, TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Check if user is authenticate
     */
    @GetMapping("/authenticate")
    public void authenticate() {
        // Empty
    }

    /**
     * Authenticate a user
     * @param auth
     * @return token
     */
    @PostMapping(value = "/signin", consumes = { "application/json" })
    public ResponseEntity<String> signin(@Valid @RequestBody Auth auth) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(auth.getEmail(), auth.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
            return new ResponseEntity<>(tokenProvider.createToken(auth.getEmail()), HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Register a new user
     * @param user
     * @return token
     */
    @PostMapping(value = "/signup", consumes = { "application/json" })
    public ResponseEntity<String> signup(@Valid @RequestBody User user) {
        if (userService.emailExists(user.getEmail())) {
            return new ResponseEntity<>("Email already exist", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return new ResponseEntity<>(tokenProvider.createToken(user.getEmail()), HttpStatus.CREATED);
    }

    /**
     * Update user password
     * @param userId
     * @param newPassword
     * @return
     */
    @PutMapping(value = "/api/users/{userId}/updatepassword", consumes = { "application/json" })
    public ResponseEntity updatePassword(@PathVariable("userId") Long userId, @Valid @RequestBody NewPassword newPassword) {
        Optional<User> optionalUser = userService.getUserById(userId);
        if(optionalUser.isEmpty()) {
            return new ResponseEntity<>("User with id {"+userId+"} is not found", HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();

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
    @PutMapping(value = "/api/users/{userId}/updateemail", consumes = { "application/json" })
    public ResponseEntity updateEmail(@PathVariable("userId") Long userId, @Valid @RequestBody NewEmail newEmail) {
        Optional<User> optionalUser = userService.getUserById(userId);
        if(optionalUser.isEmpty()) {
            return new ResponseEntity<>("User with id {"+userId+"} is not found", HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();

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
     * @return
     */
    @DeleteMapping(value = "/api/users/{userId}")
    public ResponseEntity deleteUser(HttpServletRequest request, @PathVariable("userId") Long userId) {
        Optional<User> optionalUser = userService.getUserById(userId);
        if(optionalUser.isEmpty()) {
            return new ResponseEntity<>("User with id {"+userId+"} is not found", HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();

        String requestEmail = tokenProvider.getUsername(JWTFilter.resolveToken(request));
        if(!user.getEmail().equals(requestEmail)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        userService.deleteUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
