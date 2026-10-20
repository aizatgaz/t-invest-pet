package org.invest.services;

import lombok.RequiredArgsConstructor;
import org.invest.api.SandboxApi;
import org.invest.dto.db.Account;
import org.invest.dto.sandbox_service.*;
import org.invest.dto.sandbox_service.GetSandboxAccountRequest.AccountStatus;
import org.invest.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.invest.dto.sandbox_service.GetSandboxAccountRequest.AccountStatus.ACCOUNT_STATUS_OPEN;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final SandboxApi sandboxApi;
    private final AccountRepository accountRepository;

    @Transactional
    public List<SandboxAccountViewResponse> findAccountsByStatus(
            AccountStatus status
    ) {
        saveAccounts(status);

        var response = new ArrayList<SandboxAccountViewResponse>();
        for (Account account : accountRepository.findByStatus(status.name())) {
            response.add(new SandboxAccountViewResponse()
                    .setAccountId(account.getAccountId())
                    .setName(account.getName())
                    .setStatus(account.getStatus())
                    .setTotalAmountRub(account.getTotalAmountRub())
                    .setUpdatedAt(account.getUpdatedAt()));
        }
        return response;
    }

    private void saveAccounts(
            AccountStatus status
    ) {
        var request = new GetSandboxAccountRequest()
                .setStatus(status);
        var getSandboxAccountResponse = sandboxApi.getSandboxAccount(request);
        var idsInSandbox = getSandboxAccountResponse.getAccounts()
                .stream()
                .filter(acc -> acc.getStatus().equals(status.name()))
                .map(GetSandboxAccountResponse.Account::getId)
                .sorted()
                .toList();

        var idsInDb = accountRepository.findByStatus(status.name())
                .stream()
                .map(Account::getAccountId)
                .sorted()
                .toList();

        var idsToCreate = new ArrayList<>(idsInSandbox);
        idsToCreate.removeAll(idsInDb);
        idsToCreate.forEach(id -> createAccount(status, getSandboxAccountResponse, id));
        deleteAccounts(idsInDb, idsToCreate);
    }

    private void createAccount(AccountStatus status, GetSandboxAccountResponse getSandboxAccountResponse, UUID id) {
        var account = getSandboxAccountResponse.getAccounts()
                .stream()
                .filter(acc -> id.equals(acc.getId()))
                .findFirst()
                .get();
        var sandboxPortfolio = sandboxApi.getSandboxPortfolio(
                new GetSandboxPortfolioRequest()
                        .setCurrency(GetSandboxPortfolioRequest.Currency.RUB)
                        .setAccountId(id)
        );
        var totalAmount = BigDecimal.valueOf(Long.parseLong(sandboxPortfolio.getTotalAmountPortfolio().getUnits()));
        accountRepository.save(new Account()
                .setAccountId(account.getId())
                .setStatus(status.name())
                .setName(account.getName())
                .setTotalAmountRub(totalAmount)
                .setUpdatedAt(LocalDateTime.now()));
    }

    private void deleteAccounts(
            List<UUID> idsInDb,
            ArrayList<UUID> idsToCreate
    ) {
        var idsToDelete = new ArrayList<>(idsInDb);
        idsToDelete.removeAll(idsToCreate);
        idsToDelete.forEach(accountRepository::deleteById);
    }

    @Transactional
    public SandboxAccountViewResponse createAccount(
            String name
    ) {
        var sandboxAccountResponse = sandboxApi.openSandboxAccount(
                new OpenSandboxAccountRequest().setName(name)
        );
        var account = new Account()
                .setTotalAmountRub(BigDecimal.ZERO)
                .setName(name)
                .setAccountId(sandboxAccountResponse.getAccountId())
                .setStatus(ACCOUNT_STATUS_OPEN.name())
                .setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        return new SandboxAccountViewResponse()
                .setAccountId(account.getAccountId())
                .setName(account.getName())
                .setStatus(account.getStatus())
                .setTotalAmountRub(account.getTotalAmountRub())
                .setUpdatedAt(account.getUpdatedAt());
    }
}
