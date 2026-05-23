package com.settleFlow.modules.shared.security;

import com.settleFlow.modules.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException("Not authenticated", HttpStatus.UNAUTHORIZED);
        }
        return (Long) auth.getPrincipal();
    }
}