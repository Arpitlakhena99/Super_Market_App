package com.example.supermarket.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private Authentication authentication;
    private String testSecret = "test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm-minimum-32-bytes";
    private long testExpiration = 86400000; // 24 hours

    @BeforeEach
    void setup() {
        authentication = new UsernamePasswordAuthenticationToken("testuser", "password", new ArrayList<>());
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", testExpiration);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        // Act
        String token = jwtTokenProvider.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void generateTokenFromUsername_ShouldReturnValidToken() {
        // Act
        String token = jwtTokenProvider.generateTokenFromUsername("testuser");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUsernameFromToken_WithValidToken_ShouldReturnUsername() {
        // Arrange
        String token = jwtTokenProvider.generateTokenFromUsername("testuser");

        // Act
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtTokenProvider.generateTokenFromUsername("testuser");

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken("invalid.token.here");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getUsernameFromToken_WithInvalidToken_ShouldReturnNull() {
        // Act
        String username = jwtTokenProvider.getUsernameFromToken("invalid.token.here");

        // Assert
        assertNull(username);
    }
}

