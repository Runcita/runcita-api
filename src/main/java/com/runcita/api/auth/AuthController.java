package com.runcita.api.auth;

import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.Auth;
import com.runcita.api.shared.models.User;
import com.runcita.api.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
     * @param response
     * @return token
     */
    @PostMapping(value = "/signin", consumes = { "application/json" })
    public String signin(@Valid @RequestBody Auth auth, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(auth.getEmail(), auth.getPassword());

        try {
            this.authenticationManager.authenticate(authenticationToken);
            return this.tokenProvider.createToken(auth.getEmail());
        } catch (AuthenticationException e) {
            log.info("Security exception {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }

    /**
     * Register a new user
     * @param user
     * @return token
     */
    @PostMapping(value = "/signup", consumes = { "application/json" })
    public String signup(@Valid @RequestBody User user, HttpServletResponse response) {
        if (this.userService.emailExists(user.getEmail())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Email already exist";
        }

        user.encodePassword(this.passwordEncoder);
        this.userService.save(user);
        return this.tokenProvider.createToken(user.getEmail());
    }
}
