package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransaccionDTO implements Serializable {

    private Long idTransaccion;
    private Date fechaRegistro;
    private BigDecimal monto;
    private CuentaDTO cuenta;
    private TipoTransaccionDTO tipoTransaccion;
    private EmpleadoDTO empleado;
    private PedidoDTO pedido;
}