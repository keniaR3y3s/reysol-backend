package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetodoPagoDTO {
    private Long idMetodoPago;
    private String nombre;
    private Boolean estatus;
    private String descripcion;
}