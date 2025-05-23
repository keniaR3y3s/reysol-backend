package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {

    private Long idUsuario;
    private String usuario;
    private String contrasena;
    private Boolean estatus;
    private List<RolDTO> roles;

}