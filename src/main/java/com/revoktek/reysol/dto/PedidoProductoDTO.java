package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoProductoDTO {

    private Long idPedidoProducto;
    private Boolean estatus;
    private Integer tipoPrecio;
    private BigDecimal precio;
    private BigDecimal subtotal;

    private BigDecimal cantidadSolicitada;
    private BigDecimal cantidadDespachada;
    private BigDecimal cantidadEntregada;

    private BigDecimal pesoSolicitado;
    private BigDecimal pesoDespachado;
    private BigDecimal pesoEntregado;

    private BigDecimal diferencia;
    private PedidoDTO pedido;
    private InventarioDTO inventario;
    private ProductoDTO producto;
    private CorteHistorialDTO corteHistorial;
    private CorteDTO corte;
    private PrecioClienteDTO precioCliente;
    private ProductoCancelacionDTO productoCancelacion;

}