package com.alkemy.java2.clase15.services;

import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.mapper.UserMapper;
import com.alkemy.java2.clase15.model.User;
import com.alkemy.java2.clase15.repository.UserRepository;
import com.alkemy.java2.clase15.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  private static final String USER_ID = "123";

  private static final User USER = User.builder()
      .id(USER_ID)
      .name("usuario administrador")
      .username("admin")
      .password("admin123")
      .build();

  private static final UserDTO USER_DTO = UserDTO.builder()
      .id(USER_ID)
      .name("usuario administrador")
      .username("admin")
      .password("admin123")
      .build();

  @Test
  void getAllUsers_shouldReturnListOfUserDTO() {
    // Arrange
    when(userRepository.findAll()).thenReturn(List.of(USER));
    when(userMapper.toDTO(USER)).thenReturn(USER_DTO);

    // Act
    List<UserDTO> users = userService.getAllUsers();

    // Assert
    assertEquals(1, users.size());
    assertEquals("usuario administrador", users.get(0).getName());
    verify(userRepository).findAll();
  }

  @Test
  void createUser_shouldSaveAndReturnUserDTO() {
    // Arrange
    when(userMapper.toEntity(USER_DTO)).thenReturn(USER);
    when(userRepository.save(USER)).thenReturn(USER);
    when(userMapper.toDTO(USER)).thenReturn(USER_DTO);

    // Act
    UserDTO created = userService.createUser(USER_DTO);

    // Assert
    assertNotNull(created);
    assertEquals("admin", created.getUsername());
    verify(userRepository).save(USER);
  }

  @Test
  void getUserById_whenFound_shouldReturnUserDTO() {
    // Arrange
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
    when(userMapper.toDTO(USER)).thenReturn(USER_DTO);

    // Act
    Optional<UserDTO> result = userService.getUserById(USER_ID);

    // Assert
    assertTrue(result.isPresent());
    assertEquals("admin", result.get().getUsername());
    verify(userRepository).findById(USER_ID);
  }

  @Test
  void updateUser_whenExists_shouldUpdateAndReturnDTO() {
    // Arrange
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
    when(userRepository.save(USER)).thenReturn(USER);
    when(userMapper.toDTO(USER)).thenReturn(USER_DTO);

    // Act
    UserDTO updated = userService.updateUser(USER_ID, USER_DTO);

    // Assert
    assertEquals("usuario administrador", updated.getName());
    verify(userRepository).save(USER);
  }

  @Test
  void updateUser_whenNotExists_shouldThrowException() {
    // Arrange
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> userService.updateUser(USER_ID, USER_DTO));

    assertEquals("User not found with id: 123", ex.getMessage());
    verify(userRepository).findById(USER_ID);
  }

  @Test
  void deleteUser_whenExists_shouldDeleteUser() {
    // Arrange
    when(userRepository.existsById(USER_ID)).thenReturn(true);

    // Act
    userService.deleteUser(USER_ID);

    // Assert
    verify(userRepository).deleteById(USER_ID);
  }

  @Test
  void deleteUser_whenNotExists_shouldThrowException() {
    // Arrange
    when(userRepository.existsById(USER_ID)).thenReturn(false);

    // Act & Assert
    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> userService.deleteUser(USER_ID));

    assertEquals("User not found with id: 123", ex.getMessage());
    verify(userRepository).existsById(USER_ID);
  }
}
