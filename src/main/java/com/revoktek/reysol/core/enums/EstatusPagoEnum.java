package com.revoktek.reysol.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstatusPagoEnum {

    NO_ASIGNACION(1),
    PENDIENTE_COBRO(2),
    AUTORIZADO_COBRO(3),
    PAGO_INCOMPLETO(4),
    PAGO_COMPLETO(5),
    PAGO_CANCELADO(6);

    private final Integer value;


}
