package com.edugord.server_side_event_poc.service;

import com.edugord.server_side_event_poc.exception.JwtVerificationException;
import com.edugord.server_side_event_poc.exception.SignInException;
import com.edugord.server_side_event_poc.model.request.SignInRequest;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    public static final String ISSUER = "sse-spring-boot-poc";
    public static final String SIGN_IN_ERROR_MESSAGE_TEMPLATE = "Error while generating token. Error: %s";

    private final String secretKey;
    private final JWSHeader header;
    private final Integer tokenExpirationInSeconds;

    public JwtService(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.default-expiration-seconds}") String tokenExpirationInSeconds)
            throws NumberFormatException {
        this.secretKey = secretKey;
        this.header = new JWSHeader.Builder(JWSAlgorithm.HS256).build();
        this.tokenExpirationInSeconds = Integer.parseInt(tokenExpirationInSeconds);
    }

    public String signIn(SignInRequest signInRequest) throws SignInException {
        try {
            var claimsSet = new JWTClaimsSet.Builder()
                    .issuer(ISSUER)
                    .subject(signInRequest.username())
                    .expirationTime(Date.from(Instant.now().plusSeconds(tokenExpirationInSeconds)))
                    .issueTime(new Date())
                    .build();
            return buildToken(claimsSet);
        } catch (JOSEException e) {
            throw new SignInException(String.format(SIGN_IN_ERROR_MESSAGE_TEMPLATE, e));
        }
    }

    private String buildToken(JWTClaimsSet claimsSet) throws JOSEException {
        var jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));
        jwsObject.sign(new MACSigner(secretKey.getBytes()));
        return jwsObject.serialize();
    }

    public boolean validateToken(String token)
            throws JOSEException, ParseException, JwtVerificationException {
        var jwsObject = JWSObject.parse(token);
        var verifier = new MACVerifier(secretKey.getBytes());
        return jwsObject.verify(verifier);
    }
}
