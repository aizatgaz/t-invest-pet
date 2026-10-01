package org.invest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.invest.configurations.TInvestRestConfiguration;
import org.invest.dto.sandbox_service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import static org.invest.constants.TInvestProperties.*;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:tinvest.properties")
public class SandboxService {

    private final TInvestRestConfiguration tInvestRestConfiguration;
    private final ObjectMapper objectMapper;
    @Value("${tinvest.token}")
    private String token;

    @SneakyThrows
    public OpenSandboxAccountResponse openSandboxAccount(
            OpenSandboxAccountRequest request
    ) {
        return tInvestRestConfiguration.tInvestRestClient(token)
                .post()
                .uri(OPEN_SANDBOX_ACCOUNT)
                .body(objectMapper.writeValueAsString(request))
                .retrieve()
                .toEntity(OpenSandboxAccountResponse.class)
                .getBody();
    }

    @SneakyThrows
    public GetSandboxAccountResponse getSandboxAccount(
            GetSandboxAccountRequest request
    ) {
        return tInvestRestConfiguration.tInvestRestClient(token)
                .post()
                .uri(GET_SANDBOX_ACCOUNT)
                .body(objectMapper.writeValueAsString(request))
                .retrieve()
                .toEntity(GetSandboxAccountResponse.class)
                .getBody();
    }

    @SneakyThrows
    public GetSandboxPortfolioResponse getSandboxPortfolio(
            GetSandboxPortfolioRequest request
    ) {
        return tInvestRestConfiguration.tInvestRestClient(token)
                .post()
                .uri(GET_SANDBOX_PORTFOLIO)
                .body(objectMapper.writeValueAsString(request))
                .retrieve()
                .toEntity(GetSandboxPortfolioResponse.class)
                .getBody();
    }
}
