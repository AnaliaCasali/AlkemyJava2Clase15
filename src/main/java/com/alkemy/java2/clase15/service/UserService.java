package com.alkemy.java2.clase15.service;

import com.alkemy.java2.clase15.dto.UserDTO;

import java.util.List;

public interface UserService {
  UserDTO createUser(UserDTO user);
  List<UserDTO> getAllUsers();
  UserDTO updateUser(String id, UserDTO user);
  void  deleteUser(String id);

}
