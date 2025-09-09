package com.test.tms.services;

import com.test.tms.entities.User;
import com.test.tms.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_success() {
        User user = User.builder()
                .username("testuser")
                .password("testpass")
                .build();
        when(userRepo.findUsersByUsername("testuser")).thenReturn(user);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("testpass", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_userNotFound() {
        when(userRepo.findUsersByUsername("missinguser")).thenReturn(null);
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("missinguser"));
        assertTrue(ex.getMessage().contains("User not found"));
    }
}

