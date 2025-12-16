package com.app.milobackend.controllers;

import com.app.milobackend.dtos.UserDTO;
import com.app.milobackend.models.ClientUser;
import com.app.milobackend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from auth";
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserDTO user) {

        if (authService.exists(user.getEmail())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 409);
            errorResponse.put("message", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        ClientUser clientUser = new ClientUser();
        clientUser.setName(user.getName());
        clientUser.setEmail(user.getEmail());
        clientUser.setPasswordHash(user.getPassword());

        UserDTO returnedUser = authService.register(clientUser);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "Registered successfully");
        response.put("body", returnedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserDTO user) {

        ClientUser incomingUser = new ClientUser();
        incomingUser.setEmail(user.getEmail());
        incomingUser.setPasswordHash(user.getPassword());

        UserDTO returnedUser = authService.verify(incomingUser);
        if (returnedUser != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("body", returnedUser);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else
            return ResponseEntity.badRequest().build();
    }
}
