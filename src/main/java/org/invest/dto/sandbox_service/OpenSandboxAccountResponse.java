package org.invest.dto.sandbox_service;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class OpenSandboxAccountResponse {

    private UUID accountId;

}
