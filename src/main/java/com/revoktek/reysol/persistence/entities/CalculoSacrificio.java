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
@Table(name = "calculo_sacrificio")
public class CalculoSacrificio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calculo_sacrificio")
    private Long idCalculoSacrificio;

    @Column(name = "cantidad")
    private BigDecimal cantidad;

    @Column(name = "pendiente")
    private BigDecimal pendiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sacrificio_id")
    private Sacrificio sacrificio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_corte_id")
    private TipoCorte tipoCorte;

}