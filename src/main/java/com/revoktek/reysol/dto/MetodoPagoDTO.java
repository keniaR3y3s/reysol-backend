package com.revoktek.reysol.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoPagoDTO {
    private Long idMetodoPago;
    private String nombre;
    private Boolean estatus;
    private String descripcion;
}