package com.test.tms.services;

import com.test.tms.entities.User;
import com.test.tms.exception.CommonServiceException;
import com.test.tms.repositories.UserRepo;
import com.test.tms.requests.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_success() {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        when(userRepo.findUsersByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encoded");
        when(userRepo.save(any(User.class))).thenReturn(user);
        User result = userService.registerUser(request);
        assertEquals("testuser", result.getUsername());
        assertEquals("encoded", result.getPassword());
    }

    @Test
    void registerUser_emptyUsername() {
        UserRequest request = new UserRequest();
        request.setUsername("");
        request.setPassword("password");
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> userService.registerUser(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void registerUser_shortPassword() {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");
        request.setPassword("123");
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> userService.registerUser(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void registerUser_duplicateUsername() {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        when(userRepo.findUsersByUsername("testuser")).thenReturn(new User());
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> userService.registerUser(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void loginAttempt_success() {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        doReturn(null).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("token");
        String token = userService.loginAttempt(request);
        assertEquals("token", token);
    }

    @Test
    void loginAttempt_badCredentials() {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");
        request.setPassword("wrong");
        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> userService.loginAttempt(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}

