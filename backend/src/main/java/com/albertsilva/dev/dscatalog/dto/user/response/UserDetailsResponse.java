package com.albertsilva.dev.dscatalog.dto.user.response;

import java.util.Set;

import com.albertsilva.dev.dscatalog.dto.role.RoleResponse;

public record UserDetailsResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    Set<RoleResponse> roles) {
}