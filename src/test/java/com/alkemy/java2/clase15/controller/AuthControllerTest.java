    package com.alkemy.java2.clase15.controller;

import com.alkemy.java2.clase15.authsecurity.controller.AuthController;
import com.alkemy.java2.clase15.authsecurity.dto.AuthRequest;
import com.alkemy.java2.clase15.authsecurity.dto.AuthResponse;
import com.alkemy.java2.clase15.authsecurity.service.AuthService;
import com.alkemy.java2.clase15.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("register - caso feliz")
    void register_ReturnsAuthResponse_WhenValidRequest() {
        // Arrange: UserDTO y AuthResponse válidos
        UserDTO userDTO = UserDTO.builder()
            .name("Juan")
            .username("juan@email.com")
            .password("password123")
            .build();
        AuthResponse expected = AuthResponse.builder().token("token123").build();
        when(authService.register(userDTO)).thenReturn(expected);

        // Act
        ResponseEntity<AuthResponse> response = authController.register(userDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
        assertEquals("token123", response.getBody().getToken());
        verify(authService).register(userDTO);
    }

    @Test
    @DisplayName("register - respuesta nula del servicio")
    void register_ReturnsNullBody_WhenServiceReturnsNull() {
        // Arrange: respuesta nula
        UserDTO userDTO = UserDTO.builder()
            .name("Pedro")
            .username("pedro@email.com")
            .password("pass12345")
            .build();
        when(authService.register(userDTO)).thenReturn(null);

        // Act
        ResponseEntity<AuthResponse> response = authController.register(userDTO);

        // Assert
        assertNotNull(response);
        assertNull(response.getBody());
        verify(authService).register(userDTO);
    }

    @Test
    @DisplayName("register - error en servicio")
    void register_ThrowsException_WhenServiceFails() {
        // Arrange: simular excepción
        UserDTO userDTO = UserDTO.builder()
            .name("Ana")
            .username("ana@email.com")
            .password("password456")
            .build();
        when(authService.register(userDTO)).thenThrow(new RuntimeException("Error"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authController.register(userDTO));
        assertEquals("Error", ex.getMessage());
        verify(authService).register(userDTO);
    }

    @Test
    @DisplayName("register - request nulo")
    void register_ThrowsException_WhenRequestIsNull() {
        // Arrange: request nulo
        when(authService.register(null)).thenThrow(new IllegalArgumentException("Request nulo"));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authController.register(null));
        assertEquals("Request nulo", ex.getMessage());
        verify(authService).register(null);
    }

    @Test
    @DisplayName("login - caso feliz")
    void login_ReturnsAuthResponse_WhenValidRequest() {
        // Arrange: AuthRequest y AuthResponse válidos
        AuthRequest authRequest = AuthRequest.builder()
            .username("maria@email.com")
            .password("password789")
            .build();
        AuthResponse expected = AuthResponse.builder().token("token456").build();
        when(authService.authenticate(authRequest)).thenReturn(expected);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
        assertTrue(response.getBody().getToken().startsWith("token"));
        verify(authService).authenticate(authRequest);
    }

    @Test
    @DisplayName("login - respuesta nula del servicio")
    void login_ReturnsNullBody_WhenServiceReturnsNull() {
        // Arrange: respuesta nula
        AuthRequest authRequest = AuthRequest.builder()
            .username("nulo@email.com")
            .password("pass12345")
            .build();
        when(authService.authenticate(authRequest)).thenReturn(null);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(authRequest);

        // Assert
        assertNotNull(response);
        assertNull(response.getBody());
        verify(authService).authenticate(authRequest);
    }

    @Test
    @DisplayName("login - credenciales inválidas")
    void login_ThrowsException_WhenInvalidCredentials() {
        // Arrange: simular excepción por credenciales inválidas
        AuthRequest authRequest = AuthRequest.builder()
            .username("malo@email.com")
            .password("wrongpass")
            .build();
        when(authService.authenticate(authRequest)).thenThrow(new IllegalArgumentException("Credenciales inválidas"));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authController.login(authRequest));
        assertTrue(ex.getMessage().contains("Credenciales inválidas"));
        verify(authService).authenticate(authRequest);
    }

    @Test
    @DisplayName("login - request nulo")
    void login_ThrowsException_WhenRequestIsNull() {
        // Arrange: request nulo
        when(authService.authenticate(null)).thenThrow(new IllegalArgumentException("Request nulo"));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authController.login(null));
        assertNotNull(ex);
        assertEquals("Request nulo", ex.getMessage());
        verify(authService).authenticate(null);
    }
}