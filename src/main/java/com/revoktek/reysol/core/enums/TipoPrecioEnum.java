package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoPrecioEnum {

    PIEZA(1),
    KILO(2);

    private final Integer value;


}
