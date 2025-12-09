package com.app.milobackend.services;

import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.UserPrincipal;
import com.app.milobackend.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        ClientUser user = userRepo.findByEmail(email);

        if (user == null) {
            System.out.println("User not found (printed from inside MyUserDetailsService)");
            throw new UsernameNotFoundException(email);
        }
        return new UserPrincipal(user);
    }
}
