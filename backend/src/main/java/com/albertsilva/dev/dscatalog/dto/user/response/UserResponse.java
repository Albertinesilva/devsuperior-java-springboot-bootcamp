package com.albertsilva.dev.dscatalog.dto.user.response;

import java.util.Set;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<String> roles) {

}
