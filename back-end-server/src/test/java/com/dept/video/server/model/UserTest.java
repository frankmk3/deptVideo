package com.dept.video.server.model;

import com.dept.video.server.enums.Role;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserTest {

    @Test
    public void whenUserHasRoleReturnsAuthoritiesWithTheSameValue() {
        User user = new User();
        Role roleUser = Role.ROLE_USER;
        user.setRole(roleUser);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        Assert.assertEquals(roleUser.name(), authorities.iterator().next().getAuthority());
    }

    @Test
    public void whenUserIsAdminReturnsAuthoritiesWithRoleValue() {
        User user = new User();
        user.setAdmin(true);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        Assert.assertEquals(Role.ROLE_ADMIN.name(), authorities.iterator().next().getAuthority());
    }

    @Test
    public void whenUserRoleIsNullReturnsEmptyAuthorities() {
        User user = new User();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        Assert.assertEquals(0, authorities.size());
    }

    @Test
    public void whenChangePassIsRequiredReturnsNoneSetPasswordAuthorities() {
        User user = new User();
        user.setChangePassRequired(true);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        Assert.assertEquals(Role.ROLE_NONE_SET_PASSWORD.name(), authorities.iterator().next().getAuthority());
    }
}