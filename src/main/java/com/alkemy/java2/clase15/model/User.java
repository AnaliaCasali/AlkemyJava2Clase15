package com.alkemy.java2.clase15.model;

import com.alkemy.java2.clase15.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Document(collection = "users") // Especificar nombre de colección
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class User implements UserDetails {
  @Id
  private String id;

 @NotBlank (message = "Name es obligatorio")
  private String name;

  @Indexed(unique = true)
  @Email(message = "Email debe ser válido")
  private String username;

  @Size(min = 8, max = 20, message = "Password debe tener entre 8 y 20 caracteres")
  private String password;


  @Field("roles") // Mapea el campo roles en la colección MongoDB
  private Set<Role> roles;

  private  boolean active=true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

}