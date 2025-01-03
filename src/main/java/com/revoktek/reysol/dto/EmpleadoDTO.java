package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmpleadoDTO {
    private Long idEmpleado;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private Date fechaRegistro;
    private UsuarioDTO usuario;
    private ContactoDTO contacto;

    public EmpleadoDTO(Long idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}