package com.app.milobackend.controllers;

import com.app.milobackend.dtos.UserDTO;
import com.app.milobackend.models.ClientUser;
import com.app.milobackend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from auth";
    }

    @PostMapping("/register")
    public ResponseEntity<ClientUser> register(@RequestBody UserDTO user) {
        ClientUser clientUser = new ClientUser();

        clientUser.setEmail(user.getEmail());
        clientUser.setName(user.getName());
        clientUser.setPasswordHash(user.getPassword());

        ClientUser createdUser = authService.register(clientUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {

        ClientUser incomingUser = new ClientUser();
        incomingUser.setEmail(user.getEmail());
        incomingUser.setPasswordHash(user.getPassword());

        ClientUser verifiedUser = authService.verify(incomingUser);
        if (verifiedUser != null) {
            return ResponseEntity.ok(verifiedUser);
        }
        else
            return ResponseEntity.badRequest().build();
    }
}
