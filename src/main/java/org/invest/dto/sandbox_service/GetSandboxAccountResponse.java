package org.invest.dto.sandbox_service;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class GetSandboxAccountResponse {

    private List<Account> accounts;

    @Data
    @Accessors(chain = true)
    public static class Account {
        private OffsetDateTime openedDate;
        private OffsetDateTime closedDate;
        private String name;
        private UUID id;
        private String type;
        private String status;
    }
}
