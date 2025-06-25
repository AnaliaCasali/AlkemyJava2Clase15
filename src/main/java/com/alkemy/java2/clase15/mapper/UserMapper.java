package com.alkemy.java2.clase15.mapper;


import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.enums.Role;
import com.alkemy.java2.clase15.model.User;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

  default UserDTO toDTO(User user) {
    if (user == null) {return null;}
    Set<String> roles = null;
    if (user.getRoles() != null) {
      roles = user.getRoles()
          .stream()
          .map(Role::getName) // asumiendo que Role tiene getName()
          .collect(Collectors.toSet());
    }

    return UserDTO.builder()
        .id(user.getId())
        .name(user.getName())
        .username(user.getUsername())
        .password(user.getPassword())
        .roles(roles)
        .build();
  }

  default User toEntity(UserDTO dto) {
    if (dto == null) {return null;}

    Set<Role> roles = null;
    if (dto.getRoles() != null) {
      roles = dto.getRoles()
          .stream()
          .map(Role::valueOf) // convertir String a Enum Role
          .collect(Collectors.toSet());
    }

    return User.builder()
        .id(dto.getId())
        .name(dto.getName())
        .username(dto.getUsername())
        .password(dto.getPassword())
        .roles(roles)
        .active(true) // Asignar valor por defecto
        .build();
  }
}








