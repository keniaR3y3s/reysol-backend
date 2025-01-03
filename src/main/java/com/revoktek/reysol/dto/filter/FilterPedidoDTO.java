package com.revoktek.reysol.dto.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FilterPedidoDTO extends FilterDTO {

    private Integer idRuta;
    private Long idEmpleadoEntrega;

}
