package com.dept.video.server.common;

import com.dept.video.server.dto.BaseGrantedAuthority;
import com.dept.video.server.enums.Role;
import com.dept.video.server.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;

public class SecurityUtilityTest {

    @Test
    public void whenUserHasTheRoleReturnsTrue() {
        User principal = new User();
        principal.setRole(Role.ROLE_ADMIN);
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new BaseGrantedAuthority(principal.getRole().name()));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, "", authorities);

        boolean hasRole = SecurityUtility.hasRole(auth, Role.ROLE_ADMIN);

        Assert.assertTrue(hasRole);
    }

    @Test
    public void whenUserNoHasTheRoleReturnsFalse() {
        User principal = new User();
        principal.setRole(Role.ROLE_USER);
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new BaseGrantedAuthority(principal.getRole().name()));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, "", authorities);

        boolean hasRole = SecurityUtility.hasRole(auth, Role.ROLE_ADMIN);

        Assert.assertFalse(hasRole);
    }
}