package org.invest.dto.sandbox_service;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class GetSandboxPortfolioRequest {

    private UUID accountId;
    private Currency currency;

    public enum Currency {
        RUB,
        USD,
        EUR
    }

}
