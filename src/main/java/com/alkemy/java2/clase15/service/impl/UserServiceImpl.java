package com.alkemy.java2.clase15.service.impl;

import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.mapper.UserMapper;
import com.alkemy.java2.clase15.model.User;
import com.alkemy.java2.clase15.repository.UserRepository;
import com.alkemy.java2.clase15.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;


  @Override
  public List<UserDTO> getAllUsers() {
    return  userRepository.findAll().stream()
        .map(userMapper::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  public UserDTO createUser(UserDTO userDTO) {
    // Mapear DTO a entidad
    User user = userMapper.toEntity(userDTO);
    // Guardar entidad en la base de datos
    User savedUser = userRepository.save(user);
    // Mapear entidad guardada de vuelta a DTO
    return userMapper.toDTO(savedUser);
  }

   public Optional<UserDTO> getUserById(String id) {
    // Buscar entidad y mapear a DTO si está presente
    return userRepository.findById(id)
        .map(userMapper::toDTO);
  }

  @Override
  public UserDTO updateUser(String id, UserDTO userDTO) {
    // Obtener la entidad existente
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    // Actualizar valores desde el DTO
    existingUser.setName(userDTO.getName());
    existingUser.setUsername(userDTO.getUsername());
    existingUser.setPassword(userDTO.getPassword());
    // Guardar cambios y mapear de vuelta a DTO
    User updatedUser = userRepository.save(existingUser);
    return userMapper.toDTO(updatedUser);
  }

  @Override
  public void deleteUser(String id) {
    // Intentar borrar la entidad si está presente
    if (!userRepository.existsById(id)) {
      throw new RuntimeException("User not found with id: " + id);
    }
    userRepository.deleteById(id);
  }
}
