package com.revoktek.reysol.dto.filter;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class FilterDTO {

    private String busqueda;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fechaInicio;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fechaFin;
    private Integer estatus;
    private List<Integer> estatusList;

}
