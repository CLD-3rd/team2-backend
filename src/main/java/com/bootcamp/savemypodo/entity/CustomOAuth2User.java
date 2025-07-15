package com.bootcamp.savemypodo.entity;

import com.bootcamp.savemypodo.global.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User, Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String email;
    private final Role role;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(String email, Role role, Map<String, Object> attributes) {
        this.email = email;
        this.role = role;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getKey()));
    }

    @Override
    public String getName() {
        return email;
    }
}