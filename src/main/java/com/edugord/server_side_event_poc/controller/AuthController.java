package com.edugord.server_side_event_poc.controller;

import com.edugord.server_side_event_poc.model.request.SignInRequest;
import com.edugord.server_side_event_poc.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    @Autowired
    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(jwtService.signIn(request));
    }
}