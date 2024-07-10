package com.edugord.server_side_event_poc.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() throws IllegalStateException {
        throw new IllegalStateException("Utility class");
    }

    public static String getUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
