package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CorteDTO implements Serializable {
    private Long idCorte;
    private BigDecimal cantidad;
    private TipoCorteDTO tipoCorte;
    private ProductoDTO producto;

    //Variable bandera para cálculo ( no es de entidad)
    private BigDecimal faltante;

}