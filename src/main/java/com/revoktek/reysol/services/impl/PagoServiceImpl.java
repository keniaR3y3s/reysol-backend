package com.revoktek.reysol.services.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.revoktek.reysol.dto.CancelacionPagoDTO;
import com.revoktek.reysol.dto.CuentaDTO;
import com.revoktek.reysol.dto.EstatusPedidoDTO;
import com.revoktek.reysol.dto.MetodoPagoDTO;
import com.revoktek.reysol.persistence.entities.CancelacionPago;
import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Cuenta;
import com.revoktek.reysol.persistence.repositories.CancelacionPagoRepository;
import com.revoktek.reysol.persistence.repositories.CuentaRepository;
import com.revoktek.reysol.services.CuentaService;
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
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.EstatusPago;
import com.revoktek.reysol.persistence.entities.EstatusPedido;
import com.revoktek.reysol.persistence.entities.FormaPago;
import com.revoktek.reysol.persistence.entities.MetodoPago;
import com.revoktek.reysol.persistence.entities.Pago;
import com.revoktek.reysol.persistence.entities.Pedido;
import com.revoktek.reysol.persistence.repositories.PagoRepository;
import com.revoktek.reysol.persistence.repositories.PedidoRepository;
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
    private final JwtService jwtService;
    private final CancelacionPagoRepository cancelacionPagoRepository;
    private final CuentaService cuentaService;

    @Override
    @Transactional
    public void savePayment(PagoDTO pagoDTO, String token) throws ServiceLayerException {
        try {

            System.out.println("token: " + token);
            Optional<Pedido> pedidoOptional = pedidoRepository.findById(pagoDTO.getPedido().getIdPedido());
            if (pedidoOptional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(pagoDTO.getPedido().getIdPedido()));
            }

            Pedido pedido = pedidoOptional.get();
            Date now = applicationUtil.isNull(pagoDTO.getFechaRegistro()) ? new Date() : pagoDTO.getFechaRegistro();
            Empleado empleado = jwtService.getEmpleado(token);
            System.out.println("empleado: "+empleado);
            Integer idFormaPago = getFormaPago(pagoDTO);

            if (applicationUtil.isNull(pedido.getMetodoPago())) {
                Integer id = getMetodoPago(pagoDTO.getMonto(), pedido.getTotal());
                pedido.setMetodoPago(new MetodoPago(id));
            }

            CuentaDTO cuentaDTO = cuentaService.findOrSaveCuentaByCliente(pedido.getCliente().getIdCliente());
            Cuenta cuenta = new Cuenta(cuentaDTO.getIdCuenta());


            Pago pago = new Pago();
            pago.setMonto(pagoDTO.getMonto());
            pago.setFechaRegistro(now);
            pago.setPedido(pedido);
            pago.setMetodoPago(pedido.getMetodoPago());
            pago.setFormaPago(new FormaPago(idFormaPago));
            pago.setEstatusPago(new EstatusPago(EstatusPagoEnum.PAGO_COMPLETO.getValue()));
            pago.setEmpleado(empleado);
            pago.setCuenta(cuenta);
            pagoRepository.save(pago);

            List<Integer> statusList = Arrays.asList(EstatusPagoEnum.PAGO_COMPLETO.getValue(), EstatusPagoEnum.PAGO_INCOMPLETO.getValue());
            BigDecimal abonado = pagoRepository.findAbonadoByPedido(pedido.getIdPedido(), statusList);

            BigDecimal pendiente = pedido.getTotal().subtract(abonado);
            boolean completado = (pendiente.compareTo(BigDecimal.ZERO) <= 0);
            Integer estatusPago = completado ? EstatusPagoEnum.PAGO_COMPLETO.getValue() : EstatusPagoEnum.PAGO_INCOMPLETO.getValue();

            pago.setEstatusPago(new EstatusPago(estatusPago));
            pagoRepository.save(pago);


            pedido.setAbonado(abonado);
            pedido.setPendiente(pendiente);
            pedido.setEstatusPago(new EstatusPago(estatusPago));
            pedidoRepository.save(pedido);

            cuentaService.updateSaldo(pedido.getCliente().getIdCliente());

            System.out.println("pedido.getTotal(): " + pedido.getTotal());

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
                                            .nombre(pago.getFormaPago().getNombre())
                                            .descripcion(pago.getFormaPago().getDescripcion())
                                            .build() : null)
                            .metodoPago(pago.getMetodoPago() != null ?
                                    MetodoPagoDTO.builder()
                                            .idMetodoPago(pago.getMetodoPago().getIdMetodoPago())
                                            .nombre(pago.getMetodoPago().getNombre())
                                            .descripcion(pago.getMetodoPago().getDescripcion())
                                            .build() : null)
                            .estatusPago(pago.getEstatusPago() != null ?
                                    EstatusPagoDTO.builder()
                                            .idEstatusPago(pago.getEstatusPago().getIdEstatusPago())
                                            .nombre(pago.getEstatusPago().getNombre())
                                            .build() : null)
                            .cancelacionPago(pago.getCancelacionPago() != null ?
                                    CancelacionPagoDTO.builder()
                                            .idCancelacionPago(pago.getCancelacionPago().getIdCancelacionPago())
                                            .fechaRegistro(pago.getCancelacionPago().getFechaRegistro())
                                            .motivo(pago.getCancelacionPago().getMotivo())
                                            .empleado(EmpleadoDTO.builder()
                                                            .idEmpleado(pago.getCancelacionPago().getEmpleado().getIdEmpleado())
                                                            .nombre(pago.getCancelacionPago().getEmpleado().getNombre())
                                                            .primerApellido(pago.getCancelacionPago().getEmpleado().getPrimerApellido())
                                                            .segundoApellido(pago.getCancelacionPago().getEmpleado().getPrimerApellido())
                                                            .build() )
                                            .build() : null)
                            .empleado(pago.getEmpleado() != null ?
                                    EmpleadoDTO.builder()
                                            .idEmpleado(pago.getEmpleado().getIdEmpleado())
                                            .nombre(pago.getEmpleado().getNombre())
                                            .primerApellido(pago.getEmpleado().getPrimerApellido())
                                            .segundoApellido(pago.getEmpleado().getPrimerApellido())
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
    public void changeEstatusCancel(CancelacionPagoDTO ct, String token) throws ServiceLayerException {
        try {

            Long idPago = ct.getPago().getIdPago();
            String motivo = ct.getMotivo();
            Empleado empleado = jwtService.getEmpleado(token);

            Optional<Pago> pagoBusqueda = pagoRepository.findByPagoId(idPago);

            if (pagoBusqueda.isEmpty()) {
                throw new ServiceLayerException("Pago no encontrado con ID: " + idPago);
            }

            Pago pago = pagoBusqueda.get();
            Pedido pedido = pago.getPedido();

            pago.setEstatusPago(new EstatusPago(EstatusPagoEnum.PAGO_CANCELADO.getValue()));
            pagoRepository.save(pago);

            CancelacionPago cancelacionPago = new CancelacionPago();
            cancelacionPago.setFechaRegistro(new Date());
            cancelacionPago.setMotivo(motivo);
            cancelacionPago.setEmpleado(empleado);
            cancelacionPago.setPago(pago);
            cancelacionPagoRepository.save(cancelacionPago);

            List<Integer> statusList = Arrays.asList(EstatusPagoEnum.PAGO_COMPLETO.getValue(), EstatusPagoEnum.PAGO_INCOMPLETO.getValue());
            BigDecimal abonado = pagoRepository.findAbonadoByPedido(pedido.getIdPedido(), statusList);

            BigDecimal pendiente = pedido.getTotal().subtract(abonado);
            boolean completado = (pendiente.compareTo(BigDecimal.ZERO) <= 0);
            Integer estatusPago = completado ? EstatusPagoEnum.PAGO_COMPLETO.getValue() : EstatusPagoEnum.PAGO_INCOMPLETO.getValue();

            if(!completado && abonado.compareTo(BigDecimal.ZERO) == 0){
                estatusPago = EstatusPagoEnum.PAGO_CANCELADO.getValue();
            }

            pedido.setAbonado(abonado);
            pedido.setPendiente(pendiente);
            pedido.setEstatusPago(new EstatusPago(estatusPago));
            pedidoRepository.save(pedido);

            cuentaService.updateSaldo(pedido.getCliente().getIdCliente());


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

   @Override
    @Transactional
    public void changeEstatusAuthorized(PedidoDTO pedidoDTO, String token) throws ServiceLayerException {
        try {

            EstatusPago estatusPago = new EstatusPago(EstatusPagoEnum.AUTORIZADO_COBRO.getValue());
            Pedido pedido = pedidoRepository.findByIdPedido(pedidoDTO.getIdPedido());

            pedido.setEstatusPago(estatusPago);
            pedidoRepository.save(pedido);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }


}