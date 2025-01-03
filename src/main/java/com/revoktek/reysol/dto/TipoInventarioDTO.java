package com.revoktek.reysol.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TipoInventarioDTO {
    private Integer idTipoInventario;
    private String nombre;
    private Boolean estatus;
    private String descripcion;
}