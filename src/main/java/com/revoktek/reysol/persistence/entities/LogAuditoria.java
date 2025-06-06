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

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "log_auditoria")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log_auditoria")
    private Long idLog;

    @Column
    private String usuario;

    @Column(name = "metodo_http")
    private String metodoHttp;

    @Column
    private String uri;

    @Column(name = "http_status")
    private Integer httpStatus;


    @Column
    private String resultado;

    @Column(name = "datos_entrada")
    private String datosEntrada;


    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

}
