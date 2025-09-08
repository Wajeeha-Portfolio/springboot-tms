package com.test.tms.controllers;

import com.test.tms.Responses.JwtTokenResponse;
import com.test.tms.models.User;
import com.test.tms.requests.UserRequest;
import com.test.tms.services.JwtUtil;
import com.test.tms.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<?> createAuthenticationToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequest.class))
            )
            @RequestBody UserRequest authenticationRequest) throws Exception {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                authenticationRequest.getPassword()));

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtTokenResponse(token));
    }


    @PostMapping("/auth/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with encrypted password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
