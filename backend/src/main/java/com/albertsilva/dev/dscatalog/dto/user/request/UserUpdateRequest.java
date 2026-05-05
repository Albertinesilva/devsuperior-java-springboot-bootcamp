package com.albertsilva.dev.dscatalog.dto.user.request;

import java.util.Set;

public record UserUpdateRequest(
    String firstName,
    String lastName,
    String email,
    String password,
    Set<Long> roleIds) {

}
