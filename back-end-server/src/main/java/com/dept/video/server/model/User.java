package com.dept.video.server.model;

import com.dept.video.server.dto.BaseGrantedAuthority;
import com.dept.video.server.dto.FingerPrint;
import com.dept.video.server.enums.Role;
import com.dept.video.server.enums.UserSource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Document(collection = "user")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseObject implements UserDetails {

    @Indexed
    private String name;
    private String pass;
    private String source;
    private String lang;
    @Indexed
    private String email;
    private String version;

    private boolean enabled;
    private boolean changePassRequired;
    private boolean admin;
    private Role role;

    private Map<String, Object> properties;

    private FingerPrint fingerPrint;

    public User() {
        super();
        this.enabled = true;
        this.source = UserSource.INTERNAL.getValue();
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (changePassRequired) {
            return Stream.of(new BaseGrantedAuthority(Role.ROLE_NONE_SET_PASSWORD.name())).collect(Collectors.toList());
        }
        if (isAdmin()) {
            return Stream.of(new BaseGrantedAuthority(Role.ROLE_ADMIN.name())).collect(Collectors.toList());
        } else if (role != null) {
            return Stream.of(new BaseGrantedAuthority(role.name())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public String getEmail() {
        return StringUtils.isEmpty(email) ? id : email;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return getPass();
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return getId();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAdmin() {
        return admin || Role.ROLE_ADMIN.equals(role);
    }
}
