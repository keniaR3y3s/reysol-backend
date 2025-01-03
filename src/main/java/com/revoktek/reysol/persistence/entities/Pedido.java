package com.revoktek.reysol.persistence.entities;

import com.revoktek.reysol.core.annotations.IgnoreMapping;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    @Column(name = "clave", nullable = false, length = 20)
    private String clave;

    @Column(name = "fecha_solicitud")
    private Date fechaSolicitud;

    @Column(name = "fecha_registro")
    private Date fechaRegistro;

    @Column(name = "fecha_entrega")
    private Date fechaEntrega;


    @Column(name = "fecha_despacha")
    private Date fechaDespacha;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "abonado")
    private BigDecimal abonado;

    @Column(name = "pendiente")
    private BigDecimal pendiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estatus_pedido_id")
    private EstatusPedido estatusPedido;    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "estatus_pedido_previo_id")
    private EstatusPedido estatusPedidoPrevio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago_id")
    private MetodoPago metodoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domicilio_id")
    private Domicilio domicilio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_entrega_id")
    private Empleado empleadoEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_despacha_id")
    private Empleado empleadoDespacha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id")
    @IgnoreMapping
    private Ruta ruta;

    public Pedido(Long idPedido) {
        this.idPedido = idPedido;
    }
}