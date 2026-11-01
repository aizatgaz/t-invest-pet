package org.invest.services;

import org.invest.dto.auth.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.invest.enums.Roles.MANAGER;

@Service
public class JwtTokenService {
    public static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(20);

    private final JwtEncoder encoder;
    private final Clock clock;
    private final String issuer;
    private final String audience;

    public JwtTokenService(
            JwtEncoder encoder,
            Clock jwtClock,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience
    ) {
        this.encoder = encoder;
        this.clock = jwtClock;
        this.issuer = issuer;
        this.audience = audience;
    }

    public TokenResponse issueManagerToken(String supportUsername) {
        var issuedAt = clock.instant().truncatedTo(ChronoUnit.SECONDS);
        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(supportUsername)
                .audience(List.of(audience))
                .issuedAt(issuedAt)
                .notBefore(issuedAt)
                .expiresAt(issuedAt.plus(ACCESS_TOKEN_TTL))
                .id(UUID.randomUUID().toString())
                .claim("roles", List.of(MANAGER.getValue()))
                .build();
        var header = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse()
                .setAccessToken(token)
                .setTokenType("Bearer")
                .setExpiresIn(ACCESS_TOKEN_TTL.toSeconds());
    }
}
