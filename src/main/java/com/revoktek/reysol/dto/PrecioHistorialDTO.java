package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrecioHistorialDTO {
    private Long idPrecioHistorial;
    private BigDecimal precio;
    private Date fechaRegistro;
    private ProductoDTO producto;
    private EmpleadoDTO empleado;
}