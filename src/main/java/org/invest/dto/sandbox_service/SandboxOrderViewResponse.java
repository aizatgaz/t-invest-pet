package org.invest.dto.sandbox_service;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class SandboxOrderViewResponse {

    private UUID orderId;
    private UUID accountId;
    private UUID instrumentUid;
    private String direction;
    private Long quantity;
    private String orderType;
    private BigDecimal price;
    private String status;
    private LocalDateTime updatedAt;

}
