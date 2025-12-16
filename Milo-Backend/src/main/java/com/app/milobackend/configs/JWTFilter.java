package com.app.milobackend.configs;

import com.app.milobackend.services.JWTService;
import com.app.milobackend.services.MyUserDetailsService;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    private final MyUserDetailsService userDetailsService;

    public JWTFilter(JWTService jwtService, MyUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable FilterChain filterChain) throws ServletException, IOException {
        // Bearer {token}
        String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
//        System.out.println("=== JWTFilter: Processing request to: " + requestURI + " ===");
//        System.out.println("Authorization header present: " + (authHeader != null));
        
        String token = null;
        String email = null;
        if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
            token = authHeader.substring(7).trim(); // safe extraction
//            System.out.println("Token extracted (first 20 chars): " + (token.length() > 20 ? token.substring(0, 20) + "..." : token));
        }
        if (!(token == null || token.isBlank())) {
            try {
                email = jwtService.extractEmail(token);
//                System.out.println("Email extracted from token: " + email);
            } catch (Exception ex) {
                // log invalid token and continue filterChain (do not let it throw raw to filter)
                System.err.println("Failed to extract email from token: " + ex.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            System.out.println("Loading user details for: " + email);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.validateToken(token, userDetails)) {
//                System.out.println("Token validated successfully for: " + email);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                System.err.println("Token validation failed for: " + email);
            }
        } else if (email == null) {
//            System.out.println("No email extracted, skipping authentication");
        }
        
        assert filterChain != null;
        filterChain.doFilter(request, response);
    }

}
