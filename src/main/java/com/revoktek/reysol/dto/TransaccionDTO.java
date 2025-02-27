package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransaccionDTO implements Serializable {

    private Long idTransaccion;
    private Date fechaRegistro;
    private BigDecimal monto;
    private CuentaDTO cuenta;
    private TipoTransaccionDTO tipoTransaccion;
    private EmpleadoDTO empleado;
    private PedidoDTO pedido;
    private PagoDTO pago;
}