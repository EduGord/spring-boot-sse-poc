package com.edugord.server_side_event_poc.config.security;

public enum AuthorizeRequestsEnum {

    PERMIT_ALL_PATHS(new String[]{
            "/auth/sign-in",
            "/h2-console/**",
            "/favicon.ico",
            "/error",
            "/**.png",
            "/**.gif",
            "/**.svg",
            "/**.jpg",
            "/**.html",
            "/**.css",
            "/**.js"
    });
    private final String[] paths;

    AuthorizeRequestsEnum(String[] paths) {
        this.paths = paths;
    }

    public String[] getPaths() {
        return paths;
    }
}
