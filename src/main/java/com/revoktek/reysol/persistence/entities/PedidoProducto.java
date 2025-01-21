package com.revoktek.reysol.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pedido_producto")
public class PedidoProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_producto")
    private Long idPedidoProducto;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @Column(name = "cantidad_frias")
    private BigDecimal cantidadFrias;

    @Column(name = "cantidad_solicitada")
    private BigDecimal cantidadSolicitada;

    @Column(name = "peso_solicitado")
    private BigDecimal pesoSolicitado;

    @Column(name = "cantidad_despachada")
    private BigDecimal cantidadDespachada;

    @Column(name = "peso_despachado")
    private BigDecimal pesoDespachado;

    @Column(name = "cantidad_entregada")
    private BigDecimal cantidadEntregada;

    @Column(name = "peso_entregado")
    private BigDecimal pesoEntregado;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id")
    private Inventario inventario;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "precio_historial_id")
    private PrecioHistorial precioHistorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "precio_cliente_id")
    private PrecioCliente precioCliente;

}