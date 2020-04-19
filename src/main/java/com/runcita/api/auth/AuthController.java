package com.runcita.api.auth;

import com.runcita.api.config.security.jwt.JWTFilter;
import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.*;
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
 * Auth controller
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthController(PasswordEncoder passwordEncoder, AuthService authService, TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.authService = authService;
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
     * @param signin
     * @return token
     */
    @PostMapping(value = "/signin", consumes = { "application/json" })
    public ResponseEntity<String> signin(@Valid @RequestBody Signin signin) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signin.getEmail(), signin.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
            return new ResponseEntity<>(tokenProvider.createToken(signin.getEmail()), HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Recover a user authentificated
     * @return user
     */
    @GetMapping(value = "/me")
    public ResponseEntity<User> recoverUserAuthentificated(HttpServletRequest request) throws AuthNotFoundException {
        String requestEmail = tokenProvider.getUsername(JWTFilter.resolveToken(request));
        Auth auth = authService.getAuthByEmail(requestEmail);
        return new ResponseEntity<>(auth.getUser(), HttpStatus.OK);
    }

    /**
     * Register a new user
     * @param auth
     * @return token
     */
    @PostMapping(value = "/signup", consumes = { "application/json" })
    public ResponseEntity<String> signup(@Valid @RequestBody Auth auth) {
        if (authService.emailExists(auth.getEmail())) {
            return new ResponseEntity<>("Email already exist", HttpStatus.BAD_REQUEST);
        }

        auth.setPassword(passwordEncoder.encode(auth.getPassword()));
        authService.saveAuth(auth);
        return new ResponseEntity<>(tokenProvider.createToken(auth.getEmail()), HttpStatus.CREATED);
    }

    /**
     * Update user password
     * @param newPassword
     * @return
     */
    @PutMapping(value = "/updatepassword", consumes = { "application/json" })
    public ResponseEntity updatePassword(HttpServletRequest request, @Valid @RequestBody NewPassword newPassword) throws AuthNotFoundException {
        String requestEmail = tokenProvider.getUsername(JWTFilter.resolveToken(request));
        Auth auth = authService.getAuthByEmail(requestEmail);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(auth.getEmail(), newPassword.getOldPassword());
        try {
            authenticationManager.authenticate(authenticationToken);
            auth.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
            authService.saveAuth(auth);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Password is incorrect", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Update user email
     * @param newEmail
     * @return
     */
    @PutMapping(value = "/updateemail", consumes = { "application/json" })
    public ResponseEntity updateEmail(HttpServletRequest request, @Valid @RequestBody NewEmail newEmail) throws AuthNotFoundException {
        String requestEmail = tokenProvider.getUsername(JWTFilter.resolveToken(request));
        Auth auth = authService.getAuthByEmail(requestEmail);

        if (authService.emailExists(newEmail.getNewEmail())) {
            return new ResponseEntity<>("Email already exist", HttpStatus.BAD_REQUEST);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(auth.getEmail(), newEmail.getPassword());
        try {
            authenticationManager.authenticate(authenticationToken);
            auth.setEmail(newEmail.getNewEmail());
            authService.saveAuth(auth);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Password is incorrect", HttpStatus.UNAUTHORIZED);
        }
    }

    @ExceptionHandler(AuthNotFoundException.class)
    public final ResponseEntity<String> handleAuthNotFoundException(AuthNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
