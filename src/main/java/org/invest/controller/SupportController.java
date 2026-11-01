package org.invest.controller;

import lombok.RequiredArgsConstructor;
import org.invest.dto.auth.TokenResponse;
import org.invest.services.JwtTokenService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SupportController {
    private final JwtTokenService tokenService;

    @PostMapping("/api/support/auth")
    @PreAuthorize("hasRole('SUPPORT')")
    public ResponseEntity<TokenResponse> authenticate(Authentication authentication) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(tokenService.issueManagerToken(authentication.getName()));
    }
}
