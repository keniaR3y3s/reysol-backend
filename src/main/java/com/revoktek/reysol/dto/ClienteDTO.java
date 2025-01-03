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
public class ClienteDTO {
    private Long idCliente;
    private String alias;
    private Date fechaRegistro;
    private Boolean estatus;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private RutaDTO ruta;
    private TipoClienteDTO tipoCliente;

    //Atributos bidireccionales
    private ContactoDTO contacto;
    private DomicilioDTO domicilio;
}