package com.albertsilva.dev.dscatalog.dto.user.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.albertsilva.dev.dscatalog.dto.user.request.UserCreateRequest;
import com.albertsilva.dev.dscatalog.dto.user.response.UserResponse;
import com.albertsilva.dev.dscatalog.entities.Role;
import com.albertsilva.dev.dscatalog.entities.User;

@Component
public class UserMapper {

  public static User toEntity(UserCreateRequest request, Set<Role> roles) {

    User user = new User();

    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setEmail(request.email());
    user.setPassword(request.password());

    user.getRoles().addAll(roles);

    return user;
  }

  public static UserResponse toResponse(User user) {
    Set<String> roles = user.getRoles().stream().map(Role::getAuthority).collect(Collectors.toSet());

    return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), roles);
  }
}
