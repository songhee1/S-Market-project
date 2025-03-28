package com.flab.s_market.domains.security.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    PROVIDER("ROLE_USER, ROLE_PROVIDER"),
    ADMIN("ROLE_USER, ROLE_PROVIDER, ROLE_ADMIN");

    private final String roles;
    public static String getIncludingRoles(String role){
        return Role.valueOf(role).getRoles();
    }
}
