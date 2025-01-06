package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomicilioDTO {
    private Long idDomicilio;
    private String calle;
    private String numInt;
    private String numExt;
    private String colonia;
    private String municipio;
    private String estado;
    private ClienteDTO cliente;
}