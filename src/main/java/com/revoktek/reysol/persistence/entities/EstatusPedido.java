package com.revoktek.reysol.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "estatus_pedido")
public class EstatusPedido {

    public EstatusPedido(Integer idEstatusPedido) {
        this.idEstatusPedido = idEstatusPedido;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estatus_pedido")
    private Integer idEstatusPedido;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "estatus")
    private Boolean estatus;

    @Column(name = "descripcion")
    private String descripcion;

}