package com.test.tms.controllers;

import com.test.tms.Responses.JwtTokenResponse;
import com.test.tms.models.User;
import com.test.tms.requests.UserRequest;
import com.test.tms.services.JwtUtil;
import com.test.tms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    UserService userService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserRequest authenticationRequest)
            throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (Exception e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtTokenResponse(token));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest registerRequest) {
        try {
            // Basic validation
            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }

            if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 4) {
                return ResponseEntity.badRequest().body("Password must be at least 4 characters");
            }

            // Register user
            User user = userService.registerUser(registerRequest);

            return ResponseEntity.ok("User registered successfully with username: " + user.getUsername());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }
}
