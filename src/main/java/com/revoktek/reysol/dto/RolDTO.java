package com.revoktek.reysol.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RolDTO implements Serializable {

    private Integer idRol;
    private String nombre;
    private String descripcion;

}