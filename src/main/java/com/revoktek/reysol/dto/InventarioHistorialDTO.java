package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventarioHistorialDTO {

    private Long idInventarioHistorial;
    private BigDecimal cantidad;
    private BigDecimal peso;
    private Date fechaRegistro;
    private InventarioDTO inventario;
    private EmpleadoDTO empleado;
    private TipoMovimientoDTO tipoMovimiento;

}