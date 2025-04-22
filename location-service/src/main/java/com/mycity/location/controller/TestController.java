package com.mycity.location.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping
    public ResponseEntity<String> test(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok("Hello, " + body.get("name"));
    }
}
