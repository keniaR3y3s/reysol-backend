package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetodoPagoDTO {
    private Integer idMetodoPago;
    private String nombre;
    private Boolean estatus;
    private String descripcion;

    public MetodoPagoDTO(Integer idMetodoPago, String nombre) {
        this.idMetodoPago = idMetodoPago;
        this.nombre = nombre;
    }
}