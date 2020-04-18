package com.runcita.api.ping;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ping controller
 */
@CrossOrigin
@RestController
public class PingController {

    /**
     * Check if the server is started
     * @return string "pong"
     */
    @GetMapping("/ping")
    public String ping() {
        return "PONG";
    }
}
