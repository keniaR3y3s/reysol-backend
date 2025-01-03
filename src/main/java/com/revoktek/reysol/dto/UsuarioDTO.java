package com.revoktek.reysol.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsuarioDTO {

    private Long idUsuario;
    private String usuario;
    private String contrasena;
    private Boolean estatus;
    private List<RolDTO> roles;

}