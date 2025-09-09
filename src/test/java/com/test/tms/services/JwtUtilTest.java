package com.test.tms.services;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private JwtUtil jwtUtil;
    private final String testSecret = "dGVzdHNlY3JldGtleWZvcmp3dHRlc3RpbmcxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMw=="; // base64 for 'testsecretkeyforjwttesting12345678901234567890123'
    private final int testExpiration = 3600; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Use reflection to set private fields
        try {
            java.lang.reflect.Field secretField = JwtUtil.class.getDeclaredField("SECRET");
            secretField.setAccessible(true);
            secretField.set(jwtUtil, testSecret);
            java.lang.reflect.Field expField = JwtUtil.class.getDeclaredField("jwtExpiration");
            expField.setAccessible(true);
            expField.set(jwtUtil, testExpiration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generateToken_and_extractUsername_success() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void extractExpiration_success() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        Date expiration = jwtUtil.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_validToken() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_invalidUser() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otherUser = Mockito.mock(UserDetails.class);
        Mockito.when(otherUser.getUsername()).thenReturn("otheruser");
        assertFalse(jwtUtil.validateToken(token, otherUser));
    }

    @Test
    void extractClaim_customClaim() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        Claims claims = jwtUtil.extractClaim(token, c -> c);
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    void validateToken_expiredToken() throws Exception {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testuser");
        // Set expiration to -1 second (already expired)
        java.lang.reflect.Field expField = JwtUtil.class.getDeclaredField("jwtExpiration");
        expField.setAccessible(true);
        expField.set(jwtUtil, -1);
        String token = jwtUtil.generateToken(userDetails);
        assertFalse(jwtUtil.validateToken(token, userDetails));
    }
}

