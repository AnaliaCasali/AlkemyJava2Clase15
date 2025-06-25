package com.alkemy.java2.clase15.config;

import com.alkemy.java2.clase15.authsecurity.JwtAuthFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

  private final JwtAuthFilter jwtAuthFilter = mock(JwtAuthFilter.class);
  private final UserDetailsService userDetailsService = mock(UserDetailsService.class);
  private final SecurityConfig securityConfig = new SecurityConfig(jwtAuthFilter, userDetailsService);

  @Test
  @DisplayName("Constructor lanza excepción si JwtAuthFilter es nulo")
  void constructor_ThrowsException_WhenJwtAuthFilterIsNull() {
    // Assert
    NullPointerException ex = assertThrows(NullPointerException.class, () -> new SecurityConfig(null, userDetailsService));
    assertEquals("JwtAuthFilter is required", ex.getMessage());
  }

  @Test
  @DisplayName("Constructor lanza excepción si UserDetailsService es nulo")
  void constructor_ThrowsException_WhenUserDetailsServiceIsNull() {
    // Assert
    NullPointerException ex = assertThrows(NullPointerException.class, () -> new SecurityConfig(jwtAuthFilter, null));
    assertEquals("UserDetailsService is required", ex.getMessage());
  }

  @Test
  @DisplayName("securityFilterChain retorna DefaultSecurityFilterChain correctamente")
  void securityFilterChain_ReturnsDefaultSecurityFilterChain() throws Exception {
    // Arrange
    var http = mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class, RETURNS_DEEP_STUBS);
    DefaultSecurityFilterChain mockChain = new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE);
    when(http.build()).thenReturn(mockChain);

    // Act
    SecurityFilterChain chain = securityConfig.securityFilterChain(http);

    // Assert
    assertNotNull(chain);
    assertEquals(mockChain, chain);
  }

  @Test
  @DisplayName("authProvider retorna un DaoAuthenticationProvider válido")
  void authProvider_ReturnsValidProvider() {
    // Act
    AuthenticationProvider provider = securityConfig.authProvider();

    // Assert
    assertNotNull(provider);
    assertTrue(provider.supports(org.springframework.security.authentication.UsernamePasswordAuthenticationToken.class));
  }

  @Test
  @DisplayName("authManager retorna el AuthenticationManager del config")
  void authManager_ReturnsManager() throws Exception {
    // Arrange
    AuthenticationManager mockManager = mock(AuthenticationManager.class);
    AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
    when(config.getAuthenticationManager()).thenReturn(mockManager);

    // Act
    AuthenticationManager result = securityConfig.authManager(config);

    // Assert
    assertNotNull(result);
    assertEquals(mockManager, result);
  }

  @Test
  @DisplayName("authManager lanza excepción si config falla")
  void authManager_ThrowsException_WhenConfigFails() throws Exception {
    // Arrange
    AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
    when(config.getAuthenticationManager()).thenThrow(new RuntimeException("Error"));

    // Act & Assert
    RuntimeException ex = assertThrows(RuntimeException.class, () -> securityConfig.authManager(config));
    assertEquals("Error", ex.getMessage());
  }

  @Test
  @DisplayName("corsConfig retorna configuración válida para /**")
  void corsConfig_ReturnsValidCorsConfigurationSource() {
    // Arrange
    CorsConfigurationSource source = securityConfig.corsConfig();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v1/test/home");

    // Act
    var cors = source.getCorsConfiguration(request);

    // Assert
    assertNotNull(cors);
    assertTrue(cors.getAllowedOrigins().contains("http://localhost:3000"));
    assertTrue(cors.getAllowedMethods().contains("POST"));
    assertTrue(cors.getAllowedHeaders().contains("*"));
    assertTrue(Boolean.TRUE.equals(cors.getAllowCredentials()));
  }

  @Test
  @DisplayName("passwordEncoder retorna BCryptPasswordEncoder y valida contraseñas")
  void passwordEncoder_ReturnsBCrypt() {
    // Act
    PasswordEncoder encoder = securityConfig.passwordEncoder();

    // Assert
    assertNotNull(encoder);
    String raw = "clave12345";
    String encoded = encoder.encode(raw);
    assertNotNull(encoded);
    assertTrue(encoder.matches(raw, encoded));
    assertFalse(encoder.matches("otraClave", encoded));
  }
}