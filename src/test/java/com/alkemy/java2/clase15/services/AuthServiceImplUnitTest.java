package com.alkemy.java2.clase15.services;

import com.alkemy.java2.clase15.authsecurity.dto.AuthRequest;
import com.alkemy.java2.clase15.authsecurity.dto.AuthResponse;
import com.alkemy.java2.clase15.authsecurity.service.AuthServiceImpl;
import com.alkemy.java2.clase15.authsecurity.service.JwtService;
import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.enums.Role;
import com.alkemy.java2.clase15.mapper.UserMapper;
import com.alkemy.java2.clase15.model.User;
import com.alkemy.java2.clase15.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplUnitTest {

  // Constantes para datos de prueba
  private static final String USERNAME = "testuser";
  private static final String PASSWORD = "testpass";
  private static final String ENCODED_PASSWORD = "encodedTestPass";
  private static final String JWT_TOKEN = "test.jwt.token";
  private static final String USER_ID = "123";
  private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid credentials";
  private static final String USER_ALREADY_EXISTS_MESSAGE = "Username already exists";

  private static final Set<GrantedAuthority> AUTHORITIES = Collections.singleton(
      new SimpleGrantedAuthority("ROLE_USER")
  );

  private static final UserDTO USER_DTO = UserDTO.builder()
      .username(USERNAME)
      .password(PASSWORD)
      .build();

  private static final AuthRequest AUTH_REQUEST = AuthRequest.builder()
      .username(USERNAME)
      .password(PASSWORD)
      .build();

  private static final User USER = User.builder()
      .id(USER_ID)
      .username(USERNAME)
      .password(ENCODED_PASSWORD)
      .roles(Set.of(Role.USER))
      .build();

  @Mock private UserRepository userRepository;
  @Mock private UserMapper userMapper;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthServiceImpl authService;

  @Nested
  class RegisterTests {
    @Test
    void shouldThrowRuntimeException_whenUserAlreadyExists() {
      // Arrange
      when(userRepository.existsUserByUsername(USERNAME)).thenReturn(true);

      // Act
      RuntimeException exception = assertThrows(RuntimeException.class,
          () -> authService.register(USER_DTO));

      // Assert
      assertEquals(USER_ALREADY_EXISTS_MESSAGE, exception.getMessage());
      verify(userRepository).existsUserByUsername(USERNAME);
      verifyNoMoreInteractions(userMapper, passwordEncoder, userRepository, jwtService);
    }

    @Test
    void shouldReturnAuthResponse_whenUserDoesNotExist() {
      // Arrange
      when(userRepository.existsUserByUsername(USERNAME)).thenReturn(false);
      when(userMapper.toEntity(USER_DTO)).thenReturn(USER);
      when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
      when(userRepository.save(USER)).thenReturn(USER);
      when(jwtService.generateToken(USER)).thenReturn(JWT_TOKEN);

      // Act
      AuthResponse result = authService.register(USER_DTO);

      // Assert
      assertAll(
          () -> assertNotNull(result),
          () -> assertEquals(JWT_TOKEN, result.getToken())
      );
      verify(userRepository).existsUserByUsername(USERNAME);
      verify(userMapper).toEntity(USER_DTO);
      verify(passwordEncoder).encode(PASSWORD);
      verify(userRepository).save(USER);
      verify(jwtService).generateToken(USER);
    }

    @Test
    void shouldCreateUserWithEncodedPassword() {
      // Arrange
      UserDTO newUserDto = UserDTO.builder()
          .username("newuser")
          .password("newpass123")
          .roles(Set.of("USER"))
          .build();

      User newUser = User.builder()
          .username("newuser")
          .password("encodedNewPass123")
          .roles(Set.of(Role.USER))
          .build();

      when(userRepository.existsUserByUsername("newuser")).thenReturn(false);
      when(userMapper.toEntity(newUserDto)).thenReturn(newUser);
      when(passwordEncoder.encode("newpass123")).thenReturn("encodedNewPass123");
      when(userRepository.save(newUser)).thenReturn(newUser);
      when(jwtService.generateToken(newUser)).thenReturn("new.jwt.token");

      // Act
      AuthResponse result = authService.register(newUserDto);

      // Assert
      assertAll(
          () -> assertNotNull(result),
          () -> assertEquals("new.jwt.token", result.getToken())
      );
      verify(passwordEncoder).encode("newpass123");
      verify(userRepository).save(argThat(user ->
          "encodedNewPass123".equals(user.getPassword())
      ));
    }
  }

  @Nested
  class AuthenticateTests {
    @Test
    void shouldReturnAuthResponse_whenCredentialsAreValid() {
      // Arrange
      Authentication auth = new UsernamePasswordAuthenticationToken(
          USERNAME,
          PASSWORD,
          AUTHORITIES
      );

      when(authenticationManager.authenticate(any())).thenReturn(auth);
      when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(USER));
      when(jwtService.generateToken(USER)).thenReturn(JWT_TOKEN);

      // Act
      AuthResponse result = authService.authenticate(AUTH_REQUEST);

      // Assert
      assertAll(
          () -> assertNotNull(result),
          () -> assertEquals(JWT_TOKEN, result.getToken())
      );
      verify(authenticationManager).authenticate(any());
      verify(userRepository).findByUsername(USERNAME);
      verify(jwtService).generateToken(USER);
    }

    @Test
    void shouldThrowBadCredentialsException_whenAuthenticationFails() {
      // Arrange
      when(authenticationManager.authenticate(any()))
          .thenThrow(new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE));

      // Act
      BadCredentialsException exception = assertThrows(BadCredentialsException.class,
          () -> authService.authenticate(AUTH_REQUEST));

      // Assert
      assertEquals(INVALID_CREDENTIALS_MESSAGE, exception.getMessage());
      verify(authenticationManager).authenticate(any());
      verifyNoInteractions(userRepository, jwtService);
    }

    @Test
    void shouldLogUserAuthorities_whenAuthenticationIsSuccessful() {
      // Arrange
      User userWithRoles = User.builder()
          .id("456")
          .username("adminuser")
          .password("encodedAdminPass")
          .roles(Set.of(Role.ADMIN, Role.USER))
          .build();

      Authentication auth = new UsernamePasswordAuthenticationToken(
          "adminuser",
          "adminpass",
          Set.of(
              new SimpleGrantedAuthority("ROLE_ADMIN"),
              new SimpleGrantedAuthority("ROLE_USER")
          )
      );

      when(authenticationManager.authenticate(any())).thenReturn(auth);
      when(userRepository.findByUsername("adminuser")).thenReturn(Optional.of(userWithRoles));
      when(jwtService.generateToken(userWithRoles)).thenReturn("admin.jwt.token");

      // Act
      AuthResponse result = authService.authenticate(
          AuthRequest.builder()
              .username("adminuser")
              .password("adminpass")
              .build()
      );

      // Assert
      assertNotNull(result);
      verify(authenticationManager).authenticate(argThat(authRequest ->
          "adminuser".equals(authRequest.getPrincipal()) &&
              "adminpass".equals(authRequest.getCredentials())
      ));
      verify(userRepository).findByUsername("adminuser");
    }
  }
}