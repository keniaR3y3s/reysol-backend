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
    AUTORIZADO_COBRO(5),
    COBRADO(6),
    PENDIENTE_DE_COBRO(7),
    PAGO_INCOMPLETO(8),
    CANCELADO(7);

    private final Integer value;


}
