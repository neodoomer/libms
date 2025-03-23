package com.maids.libms.service;

import com.maids.libms.dto.AuthRequest;
import com.maids.libms.dto.AuthResponse;
import com.maids.libms.model.Role;
import com.maids.libms.model.User;
import com.maids.libms.repository.UserRepository;
import com.maids.libms.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private AuthRequest validRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        validRequest = new AuthRequest("user@example.com", "password");

        testUser = User.builder()
                .email("user@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void register_ShouldCreateUserAndReturnTokens() {
        // Arrange
        when(passwordEncoder.encode(validRequest.getPassword()))
                .thenReturn("encodedPassword");

        // Capture any user instance for token generation
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1);
            return savedUser; // Simulate persisted user
        });

        // Match any User argument for token generation
        when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        // Act
        AuthResponse response = authService.register(validRequest);

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");

        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void login_ShouldReturnTokens_WhenCredentialsValid() {
        // Arrange
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("refreshToken");

        // Act
        AuthResponse response = authService.login(validRequest);

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail(validRequest.getEmail());
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                authService.login(validRequest));
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_ShouldThrow_WhenAuthenticationFails() {
        // Arrange
        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () ->
                authService.login(validRequest));
    }

    @Test
    void refreshToken_ShouldReturnNewAccessToken_WhenValidRefreshToken() {
        // Arrange
        String validRefreshToken = "Bearer valid.refresh.token";
        when(jwtService.extractUsername("valid.refresh.token")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid("valid.refresh.token", testUser)).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("newAccessToken");

        // Act
        AuthResponse response = authService.refreshToken(validRefreshToken);

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("valid.refresh.token");
    }

    @Test
    void refreshToken_ShouldThrow_WhenInvalidTokenFormat() {
        // Arrange
        String invalidToken = "InvalidToken";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                authService.refreshToken(invalidToken));
    }

    @Test
    void refreshToken_ShouldThrow_WhenUserNotFound() {
        // Arrange
        String validToken = "Bearer valid.token";
        when(jwtService.extractUsername("valid.token")).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                authService.refreshToken(validToken));
    }

    @Test
    void refreshToken_ShouldThrow_WhenTokenInvalid() {
        // Arrange
        String invalidToken = "Bearer invalid.token";
        when(jwtService.extractUsername("invalid.token")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid("invalid.token", testUser)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                authService.refreshToken(invalidToken));
    }
}