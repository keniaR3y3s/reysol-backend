package com.revoktek.reysol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoDTO {
    private Long idPedido;
    private String clave;
    private Date fechaRegistro;
    private Date fechaSolicitud;
    private Date fechaEntrega;
    private Date fechaDespacha;
    private BigDecimal total;
    private BigDecimal abonado;
    private BigDecimal pendiente;
    private ClienteDTO cliente;
    private EstatusPedidoDTO estatusPedido;
    private EstatusPagoDTO estatusPago;
    private MetodoPagoDTO metodoPago;
    private DomicilioDTO domicilio;
    private EmpleadoDTO empleadoEntrega;
    private EmpleadoDTO empleadoDespacha;
    private RutaDTO ruta;
    private List<PedidoProductoDTO> productos;
    private List<PagoDTO> pagos;

    public PedidoDTO(Long idPedido) {
        this.idPedido = idPedido;
    }
}