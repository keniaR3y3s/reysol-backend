package com.revoktek.reysol.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tipo_cliente")
public class TipoCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_cliente")
    private Integer idTipoCliente;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "estatus")
    private Boolean estatus;

    @Column(name = "descripcion")
    private String descripcion;

    public TipoCliente(Integer idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
    }
}