package com.revoktek.reysol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormaPagoDTO implements Serializable {
    private Integer idFormaPago;
    private String nombre;
    private Boolean estatus;
    private String descripcion;
}