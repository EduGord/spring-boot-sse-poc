package com.edugord.server_side_event_poc.config.security;

import com.edugord.server_side_event_poc.service.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.function.Supplier;


@Component
public class JwtAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    public static final String INVALID_TOKEN_ERROR_MESSAGE = "Invalid token";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_SCHEMA_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthorizationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext request) {
        var token = getJwtFromRequest(request);

        if (StringUtils.hasText(token)) {
            try {
                if (!jwtService.validateToken(token)) {
                    throw new AccessDeniedException(INVALID_TOKEN_ERROR_MESSAGE);
                }
                var signedJWT = SignedJWT.parse(token);
                var username = signedJWT.getJWTClaimsSet().getSubject();

                var auth = new UsernamePasswordAuthenticationToken(username, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
                return new AuthorizationDecision(true);
            } catch (ParseException | JOSEException e) {
                return new AuthorizationDecision(false);
            }
        }

        return new AuthorizationDecision(false);
    }

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationManager.super.verify(authentication, object);
    }

    private String getJwtFromRequest(RequestAuthorizationContext request) {
        var bearerToken = request.getRequest().getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AUTHORIZATION_SCHEMA_PREFIX)) {
            return bearerToken.substring(AUTHORIZATION_SCHEMA_PREFIX.length());
        }
        return null;
    }
}