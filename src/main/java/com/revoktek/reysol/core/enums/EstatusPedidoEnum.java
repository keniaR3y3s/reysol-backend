package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstatusPedidoEnum {

    PENDIENTE(1),
    DESPACHADO(2),
    ASIGNADO(3),
    ENTREGADO(4),
    CANCELADO(5);

    private final Integer value;


}
