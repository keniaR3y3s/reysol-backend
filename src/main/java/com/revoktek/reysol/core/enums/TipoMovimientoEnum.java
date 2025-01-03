package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoMovimientoEnum {

    ENTRADA(1),
    SALIDA(2);

    private final Integer value;


}
