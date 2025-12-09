package com.app.milobackend.services;

import com.app.milobackend.models.ClientUser;
import com.app.milobackend.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);

    public ClientUser register(ClientUser user) {
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));
        return userRepo.save(user);
    }

    public String verify(ClientUser user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPasswordHash()));

        if (authentication.isAuthenticated()) {
            return  jwtService.generateToken(user.getEmail());
        }
        return null;
    }
}
