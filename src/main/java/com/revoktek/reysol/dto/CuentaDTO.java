package com.revoktek.reysol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CuentaDTO implements Serializable {

    private Long idCuenta;
    private Date fechaRegistro;
    private Date fechaModificacion;
    private BigDecimal saldo;
    private ClienteDTO cliente;

}