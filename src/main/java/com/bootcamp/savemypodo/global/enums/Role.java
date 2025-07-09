package com.bootcamp.savemypodo.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String key;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.key));
    }
}