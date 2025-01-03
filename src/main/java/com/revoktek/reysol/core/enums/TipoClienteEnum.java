package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoClienteEnum {

    REGULAR(1),
    EXTEMPORANEO(2);

    private final Integer value;


}
