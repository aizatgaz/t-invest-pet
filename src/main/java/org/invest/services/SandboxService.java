package org.invest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.invest.configurations.TInvestRestConfiguration;
import org.invest.dto.sandbox_service.OpenSandboxAccountRequest;
import org.invest.dto.sandbox_service.OpenSandboxAccountResponse;
import org.springframework.stereotype.Service;

import static org.invest.constants.TInvestProperties.OPEN_SANDBOX_ACCOUNT_URI;

@Service
@RequiredArgsConstructor
public class SandboxService {

    private static TInvestRestConfiguration tInvestRestConfiguration;

    @SneakyThrows
    public OpenSandboxAccountResponse openSandboxAccount(OpenSandboxAccountRequest request, String token) {
        ObjectMapper objectMapper = new ObjectMapper();
        return tInvestRestConfiguration.tInvestRestClient(token)
                .post()
                .uri(OPEN_SANDBOX_ACCOUNT_URI)
                .body(objectMapper.writeValueAsString(request))
                .retrieve()
                .toEntity(OpenSandboxAccountResponse.class)
                .getBody();
    }
}
