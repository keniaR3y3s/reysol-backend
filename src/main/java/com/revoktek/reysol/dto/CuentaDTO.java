package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CuentaDTO implements Serializable {

    private Long idCuenta;
    private Date fechaRegistro;
    private Date fechaModificacion;
    private BigDecimal saldo;
    private ClienteDTO cliente;
    private List<TransaccionDTO> transacciones;

}