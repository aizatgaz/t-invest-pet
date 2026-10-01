package org.invest.controller;

import lombok.RequiredArgsConstructor;
import org.invest.dto.db.Account;
import org.invest.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.invest.constants.TInvestProperties.APP_BASE_PATH;
import static org.invest.dto.sandbox_service.GetSandboxAccountRequest.AccountStatus;

@RestController
@RequiredArgsConstructor
public class AccountController {

    @Autowired
    private final AccountService accountService;

    @GetMapping(APP_BASE_PATH)
    public List<Account> getAccounts(
            @RequestParam("status") AccountStatus status
    ) {
        return accountService.findAccountsByStatus(status);
    }

    @PostMapping(APP_BASE_PATH)
    public Account createAccount(
            @RequestParam("name") String name
    ) {
        return accountService.createAccount(name);
    }

}
