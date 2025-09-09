package com.test.tms.controllers;

import com.test.tms.Responses.JwtTokenResponse;
import com.test.tms.entities.User;
import com.test.tms.requests.UserRequest;
import com.test.tms.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class AuthController {
    @Autowired
    UserService userService;

    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserRequest authenticationRequest) {
        String token = userService.loginAttempt(authenticationRequest);
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
        User user = userService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully with username: " + user.getUsername());
    }
}
