package dev.jordanjoseph.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//temporary just for health check
@RestController
public class HelloController {

    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}
