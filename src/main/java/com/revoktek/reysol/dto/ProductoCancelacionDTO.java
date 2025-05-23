package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoCancelacionDTO {

    private Long idProductoCancelacion;
    private String motivo;
    private Date fechaRegistro;
    private EmpleadoDTO empleado;
    private PedidoProductoDTO pedidoProducto;

}