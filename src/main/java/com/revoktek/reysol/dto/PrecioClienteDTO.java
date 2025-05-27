package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrecioClienteDTO {
    private Long idPrecioCliente;
    private BigDecimal precioPieza;
    private BigDecimal precioKilo;
    private Date fechaRegistro;
    private Boolean estatus;
    private ProductoDTO producto;
    private ClienteDTO cliente;
}