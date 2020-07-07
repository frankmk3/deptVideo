package com.dept.video.server.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ROLE_ADMIN("Administrator"),
    ROLE_USER("User"),
    ROLE_NONE_SET_PASSWORD("None-Set-Password");

    @Getter
    private String label;

    Role(String label) {
        this.label = label;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
