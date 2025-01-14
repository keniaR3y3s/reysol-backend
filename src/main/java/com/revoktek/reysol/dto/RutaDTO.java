package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RutaDTO {
    private Integer idRuta;
    private String nombre;
    private Boolean estatus;
    private String descripcion;
}