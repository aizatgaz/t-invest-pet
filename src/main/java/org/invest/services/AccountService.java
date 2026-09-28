package org.invest.services;

import lombok.RequiredArgsConstructor;
import org.invest.dto.db.Account;
import org.invest.dto.sandbox_service.GetSandboxAccountRequest;
import org.invest.dto.sandbox_service.GetSandboxAccountRequest.AccountStatus;
import org.invest.dto.sandbox_service.GetSandboxAccountResponse;
import org.invest.dto.sandbox_service.GetSandboxPortfolioRequest;
import org.invest.dto.sandbox_service.OpenSandboxAccountRequest;
import org.invest.repository.JdbcAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final SandboxService sandboxService;
    @Autowired
    private final JdbcAccountRepository accountRepository;

    @Transactional
    public List<Account> findAccountsByStatus(
            AccountStatus status
    ) {
        var request = new GetSandboxAccountRequest()
                .setStatus(status);
        var getSandboxAccountResponse = sandboxService.getSandboxAccount(request);
        var idsInSandbox = getSandboxAccountResponse.getAccounts()
                .stream()
                .filter(acc -> acc.getStatus().equals(status.name()))
                .map(GetSandboxAccountResponse.Account::getId)
                .sorted()
                .toList();

        var repositoryByStatus = accountRepository.findByStatus(status.name());
        var idsInDb = repositoryByStatus.stream()
                .map(org.invest.dto.db.Account::getAccountId)
                .sorted()
                .toList();

        if (idsInDb.equals(idsInSandbox)) {
            return repositoryByStatus;
        } else {
            saveAccounts(status, idsInSandbox, idsInDb, getSandboxAccountResponse);
        }
        return accountRepository.findByStatus(status.name());
    }

    private void saveAccounts(
            AccountStatus status,
            List<UUID> idsInSandbox,
            List<UUID> idsInDb,
            GetSandboxAccountResponse getSandboxAccountResponse
    ) {
        var idsToCreate = new ArrayList<>(idsInSandbox);
        idsToCreate.removeAll(idsInDb);
        idsToCreate.forEach(id -> {
            createAccount(status, getSandboxAccountResponse, id);
        });
        deleteAccounts(idsInDb, idsToCreate);
    }

    private void createAccount(AccountStatus status, GetSandboxAccountResponse getSandboxAccountResponse, UUID id) {
        var account = getSandboxAccountResponse.getAccounts()
                .stream()
                .filter(acc -> id.equals(acc.getId()))
                .findFirst()
                .get();
        var sandboxPortfolio = sandboxService.getSandboxPortfolio(
                new GetSandboxPortfolioRequest()
                        .setCurrency(GetSandboxPortfolioRequest.Currency.RUB)
                        .setAccountId(id)
        );
        var totalAmount = BigDecimal.valueOf(Long.valueOf(sandboxPortfolio.getTotalAmountPortfolio().getUnits()));
        accountRepository.save(new Account()
                .setAccountId(account.getId())
                .setStatus(status.name())
                .setName(account.getName())
                .setTotalAmountRub(totalAmount));
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
    public Account createAccount(
            String name
    ) {
        var sandboxAccountResponse = sandboxService.openSandboxAccount(
                new OpenSandboxAccountRequest().setName(name)
        );

        return accountRepository.save(new Account()
                .setTotalAmountRub(BigDecimal.ZERO)
                .setName(name)
                .setAccountId(sandboxAccountResponse.getAccountId())
                .setStatus(ACCOUNT_STATUS_OPEN.name())
                .setUpdatedAt(LocalDateTime.now()));
    }
}
