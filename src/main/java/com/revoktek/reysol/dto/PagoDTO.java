package com.revoktek.reysol.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class PagoDTO {

    private Long idPago;
    private BigDecimal monto;
    private Date fechaRegistro;
    private PedidoDTO pedido;
    private FormaPagoDTO formaPago;
    private EstatusPagoDTO estatusPago;
    private EmpleadoDTO empleado;

}