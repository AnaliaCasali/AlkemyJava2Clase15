    package com.alkemy.java2.clase15.repository;

import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.enums.Role;
import com.alkemy.java2.clase15.mapper.UserMapper;
import com.alkemy.java2.clase15.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

  private final UserMapper userMapper = new UserMapper() {};

  @Test
  @DisplayName("toDTO - mapea correctamente un usuario completo")
  void toDTO_ShouldMapUserToDTO() {
    // Arrange
    Set<Role> roles = new HashSet<>();
    roles.add(Role.USER);
    roles.add(Role.ADMIN);
    User user = User.builder()
        .id("1")
        .name("Juan")
        .username("juan@email.com")
        .password("pass12345")
        .roles(roles)
        .build();

    // Act
    UserDTO dto = userMapper.toDTO(user);

    // Assert
    assertNotNull(dto);
    assertEquals("1", dto.getId());
    assertEquals("Juan", dto.getName());
    assertEquals("juan@email.com", dto.getUsername());
    assertEquals("pass12345", dto.getPassword());
    assertNotNull(dto.getRoles());
    assertTrue(dto.getRoles().contains("USER"));
    assertTrue(dto.getRoles().contains("ADMIN"));
  }

  @Test
  @DisplayName("toDTO - usuario sin roles retorna roles nulo")
  void toDTO_UserWithoutRoles_ReturnsNullRoles() {
    // Arrange
    User user = User.builder()
        .id("2")
        .name("Ana")
        .username("ana@email.com")
        .password("pass12345")
        .roles(null)
        .build();

    // Act
    UserDTO dto = userMapper.toDTO(user);

    // Assert
    assertNotNull(dto);
    assertNull(dto.getRoles());
  }

  @Test
  @DisplayName("toDTO - usuario nulo retorna nulo")
  void toDTO_NullUser_ReturnsNull() {
    // Arrange, Act & Assert
    assertNull(userMapper.toDTO(null));
  }

  @Test
  @DisplayName("toEntity - mapea correctamente un DTO completo")
  void toEntity_ShouldMapDTOToUser() {
    // Arrange
    Set<String> roles = new HashSet<>();
    roles.add("USER");
    roles.add("ADMIN");
    UserDTO dto = UserDTO.builder()
        .id("3")
        .name("Pedro")
        .username("pedro@email.com")
        .password("pass12345")
        .roles(roles)
        .build();

    // Act
    User user = userMapper.toEntity(dto);

    // Assert
    assertNotNull(user);
    assertEquals("3", user.getId());
    assertEquals("Pedro", user.getName());
    assertEquals("pedro@email.com", user.getUsername());
    assertEquals("pass12345", user.getPassword());
    assertNotNull(user.getRoles());
    assertTrue(user.getRoles().contains(Role.USER));
    assertTrue(user.getRoles().contains(Role.ADMIN));
    assertTrue(user.isActive());
  }

  @Test
  @DisplayName("toEntity - DTO sin roles retorna roles nulo")
  void toEntity_DTOWithoutRoles_ReturnsNullRoles() {
    // Arrange
    UserDTO dto = UserDTO.builder()
        .id("4")
        .name("Sofia")
        .username("sofia@email.com")
        .password("pass12345")
        .roles(null)
        .build();

    // Act
    User user = userMapper.toEntity(dto);

    // Assert
    assertNotNull(user);
    assertNull(user.getRoles());
  }

  @Test
  @DisplayName("toEntity - DTO nulo retorna nulo")
  void toEntity_NullDTO_ReturnsNull() {
    // Arrange, Act & Assert
    assertNull(userMapper.toEntity(null));
  }

  @Test
  @DisplayName("toEntity - roles con string inválido lanza excepción")
  void toEntity_InvalidRoleString_ThrowsException() {
    // Arrange
    Set<String> roles = new HashSet<>(Collections.singletonList("NO_EXISTE"));
    UserDTO dto = UserDTO.builder()
        .id("5")
        .name("Error")
        .username("error@email.com")
        .password("pass12345")
        .roles(roles)
        .build();

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> userMapper.toEntity(dto));
  }
}