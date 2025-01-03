package com.revoktek.reysol.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RutaDTO {
    private Integer idRuta;
    private String nombre;
    private Boolean estatus;
    private String descripcion;
}