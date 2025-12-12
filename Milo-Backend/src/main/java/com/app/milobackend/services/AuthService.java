package com.app.milobackend.services;

import com.app.milobackend.dtos.UserDTO;
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
    private FolderService folderService;

    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);

    public UserDTO register(ClientUser user) {
        String token = jwtService.generateToken(user.getEmail());
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));
        userRepo.save(user);

        createDefaultFolders(user);

        return new UserDTO(user.getName(), user.getEmail(), "", token);
    }

    public void createDefaultFolders(ClientUser user) {
        // "Starred" is usually a filter/view, not a physical folder storage
        folderService.createFolderWithUser("inbox", user);
        folderService.createFolderWithUser("sent", user);
        folderService.createFolderWithUser("drafts", user);
        folderService.createFolderWithUser("trash", user);
    }

    public UserDTO verify(ClientUser user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPasswordHash()));

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(user.getEmail());
            user = userRepo.findByEmail(user.getEmail());
            return new UserDTO(user.getName(), user.getEmail(), "", token);
        }
        return null;
    }

    public boolean exists(String email) {
        ClientUser user = userRepo.findByEmail(email);
        return (user != null);
    }
}
