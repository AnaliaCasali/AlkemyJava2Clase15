package com.alkemy.java2.clase15.services;

import com.alkemy.java2.clase15.authsecurity.JwtAuthFilter;
import com.alkemy.java2.clase15.authsecurity.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

  @Mock
  private JwtService jwtService;
  @Mock
  private UserDetailsService userDetailsService;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;
  @Mock
  private PrintWriter printWriter;

  // Subclase interna para exponer doFilterInternal como público
  static class TestableJwtAuthFilter extends JwtAuthFilter {
    public TestableJwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
      super(jwtService, userDetailsService);
    }
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, jakarta.servlet.ServletException {
      super.doFilterInternal(request, response, filterChain);
    }
  }

  private TestableJwtAuthFilter jwtAuthFilter;

  @BeforeEach
  void setUp() {
    jwtAuthFilter = new TestableJwtAuthFilter(jwtService, userDetailsService);
  }

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Salta el filtro para rutas en whitelist")
  void doFilterInternal_SkipsWhitelistedPath() throws Exception {
    // Arrange
    when(request.getServletPath()).thenReturn("/swagger-ui");

    // Act
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtService, userDetailsService);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Autentica correctamente con JWT válido")
  void doFilterInternal_ValidToken_AuthenticatesUser() throws Exception {
    // Arrange
    String token = "valid.jwt.token";
    String username = "user@email.com";
    UserDetails userDetails = new User(username, "pass", Collections.emptyList());

    when(request.getServletPath()).thenReturn("/api/secure");
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.extractUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

    // Act
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertEquals(userDetails, auth.getPrincipal());
    assertTrue(auth.isAuthenticated());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("No autentica si falta el token")
  void doFilterInternal_NoToken_DoesNotAuthenticate() throws Exception {
    // Arrange
    when(request.getServletPath()).thenReturn("/api/secure");
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    // Act
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Assert
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("No autentica si el token es inválido")
  void doFilterInternal_InvalidToken_DoesNotAuthenticate() throws Exception {
    // Arrange
    String token = "invalid.jwt.token";
    String username = "user@email.com";
    UserDetails userDetails = new User(username, "pass", Collections.emptyList());

    when(request.getServletPath()).thenReturn("/api/secure");
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.extractUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

    // Act
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Assert
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("Maneja excepción y responde 401")
  void doFilterInternal_ExceptionDuringAuth_SetsUnauthorized() throws Exception {
    // Arrange
    String token = "bad.jwt.token";
    when(request.getServletPath()).thenReturn("/api/secure");
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("JWT error"));
    when(response.isCommitted()).thenReturn(false);
    when(response.getWriter()).thenReturn(printWriter);

    // Act
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).getWriter();
    verify(printWriter).write("Authentication failed");
    verifyNoMoreInteractions(filterChain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("No escribe error si la respuesta ya está comprometida")
  void doFilterInternal_ResponseCommitted_DoesNotWriteError() throws Exception {
    // Arrange
    String token = "bad.jwt.token";
    when(request.getServletPath()).thenReturn("/api/secure");
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("JWT error"));
    when(response.isCommitted()).thenReturn(true);

    // Act
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response, never()).getWriter();
    verifyNoMoreInteractions(filterChain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("No autentica si el username extraído es nulo")
  void doFilterInternal_NullUsername_DoesNotAuthenticate() throws Exception {
    // Arrange
    String token = "token.without.username";
    when(request.getServletPath()).thenReturn("/api/secure");
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.extractUsername(token)).thenReturn(null);

    // Act
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Assert
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}