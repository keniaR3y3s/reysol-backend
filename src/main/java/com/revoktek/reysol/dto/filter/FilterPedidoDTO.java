package com.revoktek.reysol.dto.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterPedidoDTO extends FilterDTO {

    private Integer idRuta;
    private Long idEmpleadoEntrega;

}
