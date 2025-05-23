package com.revoktek.reysol.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "producto_cancelacion")
public class ProductoCancelacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_cancelacion")
    private Long idProductoCancelacion;

    @Column(name = "fecha_registro")
    private Date fechaRegistro;

    @Lob
    @Column(name = "motivo")
    private String motivo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_producto_id")
    private PedidoProducto pedidoProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

}