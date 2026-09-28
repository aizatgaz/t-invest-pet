package org.invest.dto.sandbox_service;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetSandboxAccountRequest {

    private AccountStatus status;

    public enum AccountStatus {
        ACCOUNT_STATUS_UNSPECIFIED,
        ACCOUNT_STATUS_NEW,
        ACCOUNT_STATUS_OPEN,
        ACCOUNT_STATUS_CLOSED,
        ACCOUNT_STATUS_ALL
    }
}
