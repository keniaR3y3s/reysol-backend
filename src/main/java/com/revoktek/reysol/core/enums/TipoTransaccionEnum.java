package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoTransaccionEnum {

    COBRO(1),
    ABONO(2);

    private final Integer value;


}
