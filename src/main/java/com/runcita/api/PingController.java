package com.runcita.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ping controller
 */
@RestController
public class PingController {

    /**
     * Allows to check if the server is started
     * @return string "pong"
     */
    @GetMapping("/ping")
    public String getPing() {
        return "pong";
    }
}
