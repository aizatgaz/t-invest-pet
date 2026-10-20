package org.invest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.invest.configurations.TInvestRestConfig;
import org.invest.dto.sandbox_service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:tinvest.yaml")
public class SandboxApi {

    private final TInvestRestConfig tInvestRestConfig;
    private final ObjectMapper objectMapper;

    @Value("${tinvest.path.openSandboxAccount}")
    private String openSandBoxAccount;

    @Value("${tinvest.path.getSandboxAccount}")
    private String getSandBoxAccount;

    @Value("${tinvest.path.getSandboxPortfolio}")
    private String getSandBoxPortfolio;

    @SneakyThrows
    public OpenSandboxAccountResponse openSandboxAccount(
            OpenSandboxAccountRequest request
    ) {
        return tInvestRestConfig.tInvestRestClient()
                .post()
                .uri(openSandBoxAccount)
                .body(objectMapper.writeValueAsString(request))
                .retrieve()
                .toEntity(OpenSandboxAccountResponse.class)
                .getBody();
    }

    @SneakyThrows
    public GetSandboxAccountResponse getSandboxAccount(
            GetSandboxAccountRequest request
    ) {
        return tInvestRestConfig.tInvestRestClient()
                .post()
                .uri(getSandBoxAccount)
                .body(objectMapper.writeValueAsString(request))
                .retrieve()
                .toEntity(GetSandboxAccountResponse.class)
                .getBody();
    }

    @SneakyThrows
    public GetSandboxPortfolioResponse getSandboxPortfolio(
            GetSandboxPortfolioRequest request
    ) {
        return tInvestRestConfig.tInvestRestClient()
                .post()
                .uri(getSandBoxPortfolio)
                .body(objectMapper.writeValueAsString(request))
                .retrieve()
                .toEntity(GetSandboxPortfolioResponse.class)
                .getBody();
    }
}
