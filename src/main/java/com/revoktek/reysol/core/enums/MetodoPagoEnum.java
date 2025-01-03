package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MetodoPagoEnum {

    PAGO_UNICO(1),
    PAGO_MULTIPLE(2);

    private final Integer value;


}
