package org.invest.dto.sandbox_service;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OpenSandboxAccountRequest {

    private String name;

}
