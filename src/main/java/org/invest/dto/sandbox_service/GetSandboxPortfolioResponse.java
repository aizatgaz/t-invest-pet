package org.invest.dto.sandbox_service;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetSandboxPortfolioResponse {

    private TotalAmountPortfolio totalAmountPortfolio;

    @Data
    @Accessors(chain = true)
    public static class TotalAmountPortfolio {
        private String currency;
        private String units;
        private Integer nano;
    }
}
