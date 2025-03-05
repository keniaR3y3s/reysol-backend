package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CorteHistorialDTO {
    private Long idCorteHistorial;
    private BigDecimal precio;
    private BigDecimal cantidad;
    private Date fechaRegistro;
    private CorteDTO corte;
    private EmpleadoDTO empleado;
}