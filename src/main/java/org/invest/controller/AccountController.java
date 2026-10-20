package org.invest.controller;

import lombok.RequiredArgsConstructor;
import org.invest.dto.sandbox_service.OpenSandboxAccountRequest;
import org.invest.dto.sandbox_service.SandboxAccountViewResponse;
import org.invest.services.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.invest.constants.TInvestProperties.APP_BASE_PATH;
import static org.invest.dto.sandbox_service.GetSandboxAccountRequest.AccountStatus;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping(APP_BASE_PATH)
    public List<SandboxAccountViewResponse> getAccounts(
            @RequestParam("status") AccountStatus status
    ) {
        return accountService.findAccountsByStatus(status);
    }

    @PostMapping(APP_BASE_PATH)
    public SandboxAccountViewResponse createAccount(
            @RequestBody OpenSandboxAccountRequest request
    ) {
        return accountService.createAccount(request.getName());
    }
}
