package com.revoktek.reysol.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * DTO for {@link com.revoktek.reysol.persistence.entities.TipoTransaccion}
 */
@AllArgsConstructor
@Getter
@ToString
public class TipoTransaccionDTO implements Serializable {
    private final Long idTipoTransaccion;
    private final String nombre;
}