package org.invest.dto.db;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class Order {

    private UUID orderId;
    private UUID accountId;
    private UUID instrumentId;
    private String direction;
    private Long quantity;
    private String orderType;
    private BigDecimal price;
    private String status;
    private LocalDateTime updatedAt;

}
