package com.test.tms.services;

import com.test.tms.entities.User;
import com.test.tms.exception.CommonServiceException;
import com.test.tms.repositories.UserRepo;
import com.test.tms.requests.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class UserService {
    private final UserRepo userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepository,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.error("Username is empty");
            throw new CommonServiceException(HttpStatus.BAD_REQUEST, "Username is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 4) {
            log.error("Password is empty");
            throw new CommonServiceException(HttpStatus.BAD_REQUEST, "Password must be at least 4 characters");
        }

        // Check if username already exists
        if (userRepository.findUsersByUsername(request.getUsername()) != null) {
            log.error("Username already exists");
            throw new CommonServiceException(HttpStatus.BAD_REQUEST, "Username already exists!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        log.info("User registered successfully: {}", user.getUsername());
        return userRepository.save(user);
    }

    public String loginAttempt(UserRequest request) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
                    request.getPassword()));

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            final String token = jwtUtil.generateToken(userDetails);
            return token;
        } catch (BadCredentialsException badCredentialsException){
            log.error("Invalid username or password for user: {}", request.getUsername());
            throw new CommonServiceException(HttpStatus.BAD_REQUEST, "Invalid username or password");
        }
    }
}
