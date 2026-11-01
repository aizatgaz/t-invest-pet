package org.invest.configurations;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Configuration
public class JwtConfig {

    @Bean
    Clock jwtClock() {
        return Clock.systemUTC();
    }

    @Bean
    KeyPair jwtKeyPair() throws NoSuchAlgorithmException {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(3072);
        return generator.generateKeyPair();
    }

    @Bean
    JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {
        var key = new RSAKey.Builder((RSAPublicKey) jwtKeyPair.getPublic())
                .privateKey(jwtKeyPair.getPrivate())
                .keyID("manager")
                .build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
    }

    @Bean
    JwtDecoder jwtDecoder(
            KeyPair jwtKeyPair,
            Clock jwtClock,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience
    ) {
        var decoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) jwtKeyPair.getPublic())
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();
        var timestamps = new JwtTimestampValidator(Duration.ZERO);
        timestamps.setClock(jwtClock);
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                timestamps,
                new JwtIssuerValidator(issuer),
                new JwtClaimValidator<List<String>>("aud", aud -> aud != null && aud.contains(audience)),
                new JwtClaimValidator<Instant>("exp", expiresAt ->
                        expiresAt != null && jwtClock.instant().isBefore(expiresAt)),
                new JwtClaimValidator<Instant>("iat", Objects::nonNull),
                new JwtClaimValidator<String>("sub", subject -> subject != null && !subject.isBlank())
        ));
        return decoder;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }
}
