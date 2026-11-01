package org.invest.api;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.invest.dto.sandbox_service.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:tinvest.yaml")
public class SandboxApi {

    @Qualifier("tInvestRestClient")
    private final RestClient restClient;

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
        return restClient.post()
                .uri(openSandBoxAccount)
                .body(request)
                .retrieve()
                .toEntity(OpenSandboxAccountResponse.class)
                .getBody();
    }

    @SneakyThrows
    public GetSandboxAccountResponse getSandboxAccount(
            GetSandboxAccountRequest request
    ) {
        return restClient.post()
                .uri(getSandBoxAccount)
                .body(request)
                .retrieve()
                .toEntity(GetSandboxAccountResponse.class)
                .getBody();
    }

    @SneakyThrows
    public GetSandboxPortfolioResponse getSandboxPortfolio(
            GetSandboxPortfolioRequest request
    ) {
        return restClient.post()
                .uri(getSandBoxPortfolio)
                .body(request)
                .retrieve()
                .toEntity(GetSandboxPortfolioResponse.class)
                .getBody();
    }
}
