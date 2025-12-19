package com.example.trust.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record AuthUser(int id, String email, Collection<? extends GrantedAuthority> authorities) {
}
