package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventarioDTO {
    private Long idInventario;
    private BigDecimal cantidad;
    private BigDecimal peso;
    private Date fechaRegistro;
    private ProductoDTO producto;
    private TipoInventarioDTO tipoInventario;
    private TipoMovimientoDTO tipoMovimiento;
    private Date fechaModificacion;
}