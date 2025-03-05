package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoDTO {

    private Long idProducto;
    private String nombre;
    private Boolean estatus;
    private String descripcion;
    private UnidadMedidaDTO unidadMedida;

}