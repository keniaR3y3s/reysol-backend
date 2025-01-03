package com.revoktek.reysol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.revoktek.reysol.persistence.entities.EstatusPago}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstatusPagoDTO implements Serializable {
    private Long idEstatusPago;
    private String nombre;
}