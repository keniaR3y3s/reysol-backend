package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoInventarioEnum {

    FRESCO(1),
    REFRIGERADO(2);

    private final Integer value;


}
