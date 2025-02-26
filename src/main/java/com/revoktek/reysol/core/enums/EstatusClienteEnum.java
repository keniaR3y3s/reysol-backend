package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstatusClienteEnum {

    ACTIVO(1),
    INACTIVO(0);

    private final Integer value;


}
