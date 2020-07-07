package com.dept.video.server.common;

import com.dept.video.server.dto.BaseGrantedAuthority;
import com.dept.video.server.enums.Role;
import org.springframework.security.core.Authentication;

/**
 * Utility to manage Security reusable method.
 */
public final class SecurityUtility {

    private SecurityUtility() {
        //prevent initialization.
    }

    public static boolean hasRole(Authentication auth, Role role) {
        return auth.getAuthorities()
                .stream()
                .filter(a -> a instanceof BaseGrantedAuthority)
                .map(a -> ((BaseGrantedAuthority) a).getRole())
                .anyMatch(r -> r.equals(role.name()));
    }
}