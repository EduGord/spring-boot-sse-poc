package com.edugord.server_side_event_poc.config;

import com.edugord.server_side_event_poc.config.security.AuthorizeRequestsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthorizationManager<RequestAuthorizationContext> jwtAuthorizationManager;

    @Autowired
    public SecurityConfig(AuthorizationManager<RequestAuthorizationContext> jwtAuthorizationManager) {
        this.jwtAuthorizationManager = jwtAuthorizationManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(AuthorizeRequestsEnum.PERMIT_ALL_PATHS.getPaths()).permitAll()
                                .anyRequest().access(jwtAuthorizationManager)
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .build();
    }
}