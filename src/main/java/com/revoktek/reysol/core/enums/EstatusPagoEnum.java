package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstatusPagoEnum {

    PAGADO(1),
    EN_PROCESO(2),
    RECHAZADO(3);

    private final Integer value;


}
