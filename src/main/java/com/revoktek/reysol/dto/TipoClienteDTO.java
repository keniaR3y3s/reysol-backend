package com.revoktek.reysol.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoClienteDTO {
    private Integer idTipoCliente;
    private String nombre;
    private Boolean estatus;
    private String descripcion;
}