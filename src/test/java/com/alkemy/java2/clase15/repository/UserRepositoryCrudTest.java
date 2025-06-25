
    package com.alkemy.java2.clase15.repository;

    import com.alkemy.java2.clase15.model.User;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.*;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.util.*;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class UserRepositoryCrudTest {

        @Mock
        private UserRepository userRepository;

        private User createTestUser() {
            return User.builder()
                    .id("1")
                    .name("Test User")
                    .username("testuser@email.com")
                    .password("password123")
                    .build();
        }

        @Test
        @DisplayName("save - guarda y retorna el usuario")
        void save_ShouldReturnSavedUser() {
            // Arrange
            User user = createTestUser();
            when(userRepository.save(user)).thenReturn(user);

            // Act
            User saved = userRepository.save(user);

            // Assert
            assertNotNull(saved);
            assertEquals(user, saved);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("findById - retorna usuario existente")
        void findById_ShouldReturnUser_WhenExists() {
            // Arrange
            User user = createTestUser();
            when(userRepository.findById("1")).thenReturn(Optional.of(user));

            // Act
            Optional<User> result = userRepository.findById("1");

            // Assert
            assertTrue(result.isPresent());
            assertEquals("1", result.get().getId());
            verify(userRepository).findById("1");
        }

        @Test
        @DisplayName("findById - retorna vacío si no existe")
        void findById_ShouldReturnEmpty_WhenNotExists() {
            // Arrange
            when(userRepository.findById("2")).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userRepository.findById("2");

            // Assert
            assertFalse(result.isPresent());
            verify(userRepository).findById("2");
        }

        @Test
        @DisplayName("findAll - retorna lista de usuarios")
        void findAll_ShouldReturnUserList() {
            // Arrange
            User user1 = createTestUser();
            User user2 = User.builder().id("2").name("Otro").username("otro@email.com").password("pass").build();
            List<User> users = Arrays.asList(user1, user2);
            when(userRepository.findAll()).thenReturn(users);

            // Act
            List<User> result = userRepository.findAll();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains(user1));
            assertTrue(result.contains(user2));
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("findAll - retorna lista vacía si no hay usuarios")
        void findAll_ShouldReturnEmptyList_WhenNoUsers() {
            // Arrange
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<User> result = userRepository.findAll();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("deleteById - elimina usuario existente")
        void deleteById_ShouldCallRepository() {
            // Arrange
            String id = "1";
            doNothing().when(userRepository).deleteById(id);

            // Act
            userRepository.deleteById(id);

            // Assert
            verify(userRepository).deleteById(id);
        }

        @Test
        @DisplayName("deleteById - lanza excepción si id es nulo")
        void deleteById_ShouldThrowException_WhenIdIsNull() {
            // Arrange
            doThrow(new IllegalArgumentException("Id nulo")).when(userRepository).deleteById(null);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userRepository.deleteById(null));
            assertEquals("Id nulo", ex.getMessage());
            verify(userRepository).deleteById(null);
        }

        @Test
        @DisplayName("save - lanza excepción si usuario es nulo")
        void save_ShouldThrowException_WhenUserIsNull() {
            // Arrange
            when(userRepository.save(null)).thenThrow(new IllegalArgumentException("Usuario nulo"));

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userRepository.save(null));
            assertEquals("Usuario nulo", ex.getMessage());
            verify(userRepository).save(null);
        }

        @Test
        @DisplayName("findByUsername - retorna usuario si existe")
        void findByUsername_ShouldReturnUser_WhenExists() {
            // Arrange
            User user = createTestUser();
            when(userRepository.findByUsername("testuser@email.com")).thenReturn(Optional.of(user));

            // Act
            Optional<User> result = userRepository.findByUsername("testuser@email.com");

            // Assert
            assertTrue(result.isPresent());
            assertEquals(user, result.get());
            verify(userRepository).findByUsername("testuser@email.com");
        }

        @Test
        @DisplayName("findByUsername - retorna vacío si no existe")
        void findByUsername_ShouldReturnEmpty_WhenNotExists() {
            // Arrange
            when(userRepository.findByUsername("noexiste@email.com")).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userRepository.findByUsername("noexiste@email.com");

            // Assert
            assertFalse(result.isPresent());
            verify(userRepository).findByUsername("noexiste@email.com");
        }

        @Test
        @DisplayName("existsUserByUsername - retorna true si existe")
        void existsUserByUsername_ShouldReturnTrue_WhenExists() {
            // Arrange
            when(userRepository.existsUserByUsername("testuser@email.com")).thenReturn(true);

            // Act
            boolean exists = userRepository.existsUserByUsername("testuser@email.com");

            // Assert
            assertTrue(exists);
            verify(userRepository).existsUserByUsername("testuser@email.com");
        }

        @Test
        @DisplayName("existsUserByUsername - retorna false si no existe")
        void existsUserByUsername_ShouldReturnFalse_WhenNotExists() {
            // Arrange
            when(userRepository.existsUserByUsername("noexiste@email.com")).thenReturn(false);

            // Act
            boolean exists = userRepository.existsUserByUsername("noexiste@email.com");

            // Assert
            assertFalse(exists);
            verify(userRepository).existsUserByUsername("noexiste@email.com");
        }
    }