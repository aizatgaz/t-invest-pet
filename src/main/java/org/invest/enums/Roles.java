package org.invest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Roles {
    MANAGER("MANAGER"),
    SUPPORT("SUPPORT");

    private final String value;
}
