package com.runcita.api.ping;

import com.runcita.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ping controller
 */
@RestController
public class PingController {

    @Autowired
    UserService userService;

    /**
     * Check if the server is started
     * @return string "pong"
     */
    @GetMapping("/ping")
    public String getPing() {
        return "pong";
    }
}
