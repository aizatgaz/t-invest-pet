package org.invest.dto.db;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class Account {

    private UUID accountId;
    private String name;
    private String status;
    private BigDecimal totalAmountRub;
    private LocalDateTime updatedAt;

}
