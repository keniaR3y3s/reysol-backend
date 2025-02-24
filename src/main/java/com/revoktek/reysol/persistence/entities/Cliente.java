package com.revoktek.reysol.persistence.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "alias")
    private String alias;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_registro")
    private Date fechaRegistro;

    @Column(name = "estatus")
    private Integer estatus;




    
    
    public Boolean getEstatus() {
        return this.estatus != null && this.estatus == 1;
    }

    public void setEstatus(Boolean estatus) {
        this.estatus = (estatus != null && estatus) ? 1 : 2;
    }

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "primer_apellido")
    private String primerApellido;

    @Column(name = "segundo_apellido")
    private String segundoApellido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_cliente_id")
    private TipoCliente tipoCliente;

    @OneToOne(mappedBy = "cliente", fetch = FetchType.LAZY)
    private Domicilio domicilio;

    @OneToOne(mappedBy = "cliente", fetch = FetchType.LAZY)
    private Contacto contacto;

    public Cliente(Long idCliente) {
        this.idCliente = idCliente;
    }
}