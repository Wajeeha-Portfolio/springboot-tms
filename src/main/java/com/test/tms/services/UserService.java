package com.test.tms.services;

import com.test.tms.models.User;
import com.test.tms.repositories.UserRepo;
import com.test.tms.requests.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRequest request) {
        // Check if username already exists
        if (userRepository.findUsersByUsername(request.getUsername()) != null) {
            throw new RuntimeException("Username already exists!");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }
}
