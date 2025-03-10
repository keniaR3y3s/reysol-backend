package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstatusPagoEnum {

    PAGADO(1),
    RECHAZADO(2);

    private final Integer value;


}
