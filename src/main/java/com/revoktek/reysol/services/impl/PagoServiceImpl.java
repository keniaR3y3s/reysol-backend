package com.revoktek.reysol.services.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.revoktek.reysol.services.TransaccionService;
import org.springframework.stereotype.Service;

import com.revoktek.reysol.core.enums.EstatusPagoEnum;
import com.revoktek.reysol.core.enums.EstatusPedidoEnum;
import com.revoktek.reysol.core.enums.FormaPagoEnum;
import com.revoktek.reysol.core.enums.MetodoPagoEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.EmpleadoDTO;
import com.revoktek.reysol.dto.EstatusPagoDTO;
import com.revoktek.reysol.dto.FormaPagoDTO;
import com.revoktek.reysol.dto.PagoDTO;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.TransaccionDTO;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.EstatusPago;
import com.revoktek.reysol.persistence.entities.EstatusPedido;
import com.revoktek.reysol.persistence.entities.FormaPago;
import com.revoktek.reysol.persistence.entities.MetodoPago;
import com.revoktek.reysol.persistence.entities.Pago;
import com.revoktek.reysol.persistence.entities.Pedido;
import com.revoktek.reysol.persistence.entities.Transaccion;
import com.revoktek.reysol.persistence.repositories.PagoRepository;
import com.revoktek.reysol.persistence.repositories.PedidoRepository;
import com.revoktek.reysol.services.CuentaService;
import com.revoktek.reysol.services.JwtService;
import com.revoktek.reysol.services.PagoService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@AllArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PedidoRepository pedidoRepository;
    private final MessageProvider messageProvider;
    private final ApplicationUtil applicationUtil;
    private final PagoRepository pagoRepository;
    private final CuentaService cuentaService;
    private final TransaccionService transaccionService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void savePayment(PagoDTO pagoDTO, String token) throws ServiceLayerException {
        try {

            Optional<Pedido> pedidoOptional = pedidoRepository.findById(pagoDTO.getPedido().getIdPedido());
            if (pedidoOptional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(pagoDTO.getPedido().getIdPedido()));
            }

            Pedido pedido = pedidoOptional.get();
            Date now = new Date();
            Empleado empleado = jwtService.getEmpleado(token);
            Integer idFormaPago = getFormaPago(pagoDTO);

            if (applicationUtil.isNull(pedido.getMetodoPago())) {
                Integer id = getMetodoPago(pagoDTO.getMonto(), pedido.getTotal());
                pedido.setMetodoPago(new MetodoPago(id));
            }

            TransaccionDTO transaccionDTO = new TransaccionDTO();
            transaccionDTO.setMonto(pagoDTO.getMonto());
            transaccionDTO.setEmpleado(new EmpleadoDTO(empleado.getIdEmpleado()));
            transaccionDTO.setPedido(new PedidoDTO(pedido.getIdPedido()));
            Long idTransaccion = transaccionService.saveChargue(transaccionDTO, null);


            Pago pago = new Pago();
            pago.setMonto(pagoDTO.getMonto());
            pago.setFechaRegistro(now);
            pago.setPedido(pedido);
            pago.setMetodoPago(pedido.getMetodoPago());
            pago.setFormaPago(new FormaPago(idFormaPago));
            pago.setEstatusPago(new EstatusPago(EstatusPagoEnum.PAGADO.getValue()));
            pago.setEmpleado(empleado);
            pago.setTransaccion(new Transaccion(idTransaccion));
            pagoRepository.save(pago);


            BigDecimal abonado = pagoRepository.findAbonadoByPedido(pedido.getIdPedido(), EstatusPagoEnum.PAGADO.getValue());
            BigDecimal pendiente = pedido.getTotal().subtract(abonado);
            boolean compledato = (pendiente.compareTo(BigDecimal.ZERO) <= 0);
            Integer estatusPedido = compledato ? EstatusPedidoEnum.COBRADO.getValue() : EstatusPedidoEnum.PAGO_INCOMPLETO.getValue();


            pedido.setAbonado(abonado);
            pedido.setPendiente(pendiente);
            pedido.setEstatusPedidoPrevio(new EstatusPedido(pedido.getEstatusPedido().getIdEstatusPedido()));
            pedido.setEstatusPedido(new EstatusPedido(estatusPedido));
            pedidoRepository.save(pedido);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    private Integer getMetodoPago(BigDecimal monto, BigDecimal total) {
        if (total.compareTo(monto) < 0) {
            return MetodoPagoEnum.PAGO_MULTIPLE.getValue();
        }
        return MetodoPagoEnum.PAGO_UNICO.getValue();
    }


    private Integer getFormaPago(PagoDTO pagoDTO) {
        if (applicationUtil.nonNull(pagoDTO.getFormaPago())
                && applicationUtil.nonNull(pagoDTO.getFormaPago().getIdFormaPago())) {
            return pagoDTO.getFormaPago().getIdFormaPago();
        }
        return FormaPagoEnum.EFECTIVO.getValue();
    }
@Override
@Transactional
public List<PagoDTO> findByPedido(Long idPedido) throws ServiceLayerException {
    try {

        List<Pago> pagos = pagoRepository.findByPedidoId(idPedido);

        if (pagos.isEmpty()) {
            throw new ServiceLayerException("No se encontraron pagos para el pedido con ID: " + idPedido);
        }


        return pagos.stream()
                .map((Pago pago) -> PagoDTO.builder()
                        .idPago(pago.getIdPago())
                        .monto(pago.getMonto())
                        .fechaRegistro(pago.getFechaRegistro())
                        .pedido(pago.getPedido() != null ?
                                PedidoDTO.builder()
                                        .idPedido(pago.getPedido().getIdPedido())
                                        .total(pago.getPedido().getTotal())
                                        .abonado(pago.getPedido().getAbonado())
                                        .pendiente(pago.getPedido().getPendiente())
                                        .build() : null)
                        .formaPago(pago.getFormaPago() != null ?
                                FormaPagoDTO.builder()
                                        .idFormaPago(pago.getFormaPago().getIdFormaPago())
                                        .descripcion(pago.getFormaPago().getDescripcion())
                                        .build() : null )
                        .estatusPago(pago.getEstatusPago() != null ?
                                EstatusPagoDTO.builder()
                                        .idEstatusPago(pago.getEstatusPago().getIdEstatusPago())
                                        .nombre(pago.getEstatusPago().getNombre())
                                        .build() : null)
                        .empleado(pago.getEmpleado() != null ?
                                EmpleadoDTO.builder()
                                        .idEmpleado(pago.getEmpleado().getIdEmpleado())
                                        .nombre(pago.getEmpleado().getNombre())
                                        .primerApellido(pago.getEmpleado().getPrimerApellido())
                                        .build() : null)
                        .build()
                ).toList();

    } catch (Exception e) {
        log.error("Error al buscar los pagos por idPedido: " + idPedido, e);
        throw new ServiceLayerException(e);
    }
}
@Override
@Transactional
public void changeEstatusCancel(Long idPago) throws ServiceLayerException {
    try {
       
        Optional<Pago> pagoBusqueda = pagoRepository.findByPagoId(idPago);

        if (pagoBusqueda.isEmpty()) {
            throw new ServiceLayerException("Pago no encontrado con ID: " + idPago);
        }

        Pago pago = pagoBusqueda.get();
        pago.setEstatusPago(new EstatusPago(3));
        pagoRepository.save(pago);

    } catch (Exception e) {
        log.error("Error al rechazar el pago con ID: " + idPago, e);
        throw new ServiceLayerException(e);
    }
}


}