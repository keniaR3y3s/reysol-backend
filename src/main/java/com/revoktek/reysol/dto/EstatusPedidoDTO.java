package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstatusPedidoDTO {
    private Integer idEstatusPedido;
    private String nombre;
    private Boolean estatus;
    private String descripcion;
}