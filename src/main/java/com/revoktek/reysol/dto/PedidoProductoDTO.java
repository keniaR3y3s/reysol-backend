package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoProductoDTO {

    private Long idPedidoProducto;
    private BigDecimal precio;
    private BigDecimal cantidadSolicitada;
    private BigDecimal cantidadDespachada;
    private BigDecimal diferencia;
    private BigDecimal pesoDespachado;
    private BigDecimal pesoEntregado;
    private PedidoDTO pedido;
    private InventarioDTO inventario;
    private ProductoDTO producto;
    private PrecioHistorialDTO precioHistorial;
    private PrecioClienteDTO precioCliente;
}