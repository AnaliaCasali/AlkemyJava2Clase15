package com.alkemy.java2.clase15.repository;

import com.alkemy.java2.clase15.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends MongoRepository<User, String> {

  Optional<User> findByUsername(String username);

  default boolean existsUserByUsername(String username){
    return  findByUsername(username).isPresent();
  }

}
