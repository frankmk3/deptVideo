package com.dept.video.server.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class BaseGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    @Getter
    private final String role;

    public BaseGrantedAuthority(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof BaseGrantedAuthority) {
            return role.equals(((BaseGrantedAuthority) obj).role);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }
}
