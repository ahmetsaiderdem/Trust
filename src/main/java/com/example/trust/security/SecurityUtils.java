package com.example.trust.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils(){}

    public static int requiredUserId(){
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !(a.getPrincipal() instanceof AuthUser u)){
            throw new IllegalStateException("Authentication yok");
        }
        return u.id();
    }
}
