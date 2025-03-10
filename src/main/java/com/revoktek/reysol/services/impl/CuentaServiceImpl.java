package com.revoktek.reysol.services.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.revoktek.reysol.core.enums.EstatusPedidoEnum;
import com.revoktek.reysol.dto.CancelacionPagoDTO;
import com.revoktek.reysol.dto.EmpleadoDTO;
import com.revoktek.reysol.dto.EstatusPagoDTO;
import com.revoktek.reysol.dto.EstatusPedidoDTO;
import com.revoktek.reysol.dto.FormaPagoDTO;
import com.revoktek.reysol.dto.MetodoPagoDTO;
import com.revoktek.reysol.dto.PagoDTO;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.persistence.entities.Pago;
import com.revoktek.reysol.persistence.repositories.PagoRepository;
import org.springframework.stereotype.Service;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.ClienteDTO;
import com.revoktek.reysol.dto.CuentaDTO;
import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Cuenta;
import com.revoktek.reysol.persistence.repositories.CuentaRepository;
import com.revoktek.reysol.services.CuentaService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@AllArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final PagoRepository pagoRepository;
    private final ApplicationUtil applicationUtil;


    @Override
    @Transactional
    public CuentaDTO findOrSaveCuentaByCliente(Long idCliente) throws ServiceLayerException {
        try {
            // 1. Crear una referencia al cliente
            Cliente cliente = new Cliente(idCliente);

            // 2. Buscar la cuenta del cliente en la base de datos
            Cuenta cuenta = cuentaRepository.findByCliente(idCliente);

            // 3. Si la cuenta no existe, crear una nueva
            if (applicationUtil.isNull(cuenta)) {
                cuenta = new Cuenta();
                cuenta.setCliente(cliente); // Asociar la cuenta con el cliente
                cuenta.setSaldo(BigDecimal.ZERO); // Establecer saldo inicial en 0
                cuenta.setFechaRegistro(new Date()); // Establecer la fecha de creación
                cuenta.setFechaModificacion(new Date()); // Establecer la fecha de modificación
                cuentaRepository.save(cuenta); // Guardar la nueva cuenta en la base de datos
            }

            // 4. Retornar la cuenta como un DTO
            return CuentaDTO.builder()
                    .idCuenta(cuenta.getIdCuenta())
                    .saldo(cuenta.getSaldo())
                    .fechaRegistro(cuenta.getFechaRegistro())
                    .fechaModificacion(cuenta.getFechaModificacion())
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e); // Manejo de excepciones
        }
    }

    @Transactional
    @Override
    public List<CuentaDTO> findAllByFilter(String busqueda) throws ServiceLayerException {
        try {
            List<Cuenta> cuentas = cuentaRepository.findAllByFilter(busqueda);

            // Si no hay cuentas encontradas, retorna una lista vacía
            if (applicationUtil.isEmptyList(cuentas)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }

            log.info("{} elementos encontrados.", cuentas.size());

            // Convertir la lista de entidades `Cuenta` a DTOs `CuentaDTO`
            List<CuentaDTO> dtoList = cuentas.stream().map(cuenta ->
                    CuentaDTO.builder()
                            .idCuenta(cuenta.getIdCuenta())
                            .fechaRegistro(cuenta.getFechaRegistro())
                            .fechaModificacion(cuenta.getFechaModificacion())
                            .saldo(cuenta.getSaldo())
                            .cliente(ClienteDTO.builder()
                                    .idCliente(cuenta.getCliente().getIdCliente())
                                    .nombre(cuenta.getCliente().getNombre())
                                    .primerApellido(cuenta.getCliente().getPrimerApellido())
                                    .segundoApellido(cuenta.getCliente().getSegundoApellido())
                                    .alias(cuenta.getCliente().getAlias())
                                    .build())
                            .build()
            ).toList();

            return dtoList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public CuentaDTO findById(Long idCliente) throws ServiceLayerException {
        try {
            Cuenta cuenta = cuentaRepository.findByCliente(idCliente);
            if (cuenta == null) {
                throw new ServiceLayerException("El cuenta no existe");
            }
            CuentaDTO cuentaDTO = CuentaDTO.builder()
                    .idCuenta(cuenta.getIdCuenta())
                    .fechaRegistro(cuenta.getFechaRegistro())
                    .fechaModificacion(cuenta.getFechaModificacion())
                    .saldo(cuenta.getSaldo())
                    .cliente(ClienteDTO.builder()
                            .idCliente(cuenta.getCliente().getIdCliente())
                            .nombre(cuenta.getCliente().getNombre())
                            .primerApellido(cuenta.getCliente().getPrimerApellido())
                            .segundoApellido(cuenta.getCliente().getSegundoApellido())
                            .alias(cuenta.getCliente().getAlias())
                            .build())
                    .build();

            List<PagoDTO> pagos = findAllByCuenta(cuenta.getIdCuenta());
            cuentaDTO.setPagos(pagos);

            return cuentaDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    private List<PagoDTO> findAllByCuenta(Long idCuenta) {
            try {

                List<Pago> pagos = pagoRepository.findAllByCuenta(idCuenta);

                return pagos.stream()
                        .map((Pago pago) -> PagoDTO.builder()
                                .idPago(pago.getIdPago())
                                .monto(pago.getMonto())
                                .fechaRegistro(pago.getFechaRegistro())
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
                                                        .build())
                                                .build()
                                        : null)
                                .pedido(pago.getPedido() != null ?
                                        PedidoDTO.builder()
                                                .idPedido(pago.getPedido().getIdPedido())
                                                .clave(pago.getPedido().getClave())
                                                .total(pago.getPedido().getTotal())
                                                .abonado(pago.getPedido().getAbonado())
                                                .pendiente(pago.getPedido().getPendiente())
                                                .estatusPedido(pago.getPedido().getEstatusPedido() != null ?
                                                        EstatusPedidoDTO.builder()
                                                                .idEstatusPedido(pago.getPedido().getEstatusPedido().getIdEstatusPedido())
                                                                .nombre(pago.getPedido().getEstatusPedido().getNombre())
                                                                .build()
                                                        : null)
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
                throw new ServiceLayerException(e);
            }

    }

    @Override
    @Transactional
    public void updateSaldo(Long idCliente) throws ServiceLayerException {

        Cuenta cuenta = cuentaRepository.findByCliente(idCliente);

        if (applicationUtil.isNull(cuenta)) {
            cuenta = new Cuenta();
            cuenta.setCliente(new Cliente(idCliente));
            cuenta.setSaldo(BigDecimal.ZERO);
            cuenta.setFechaRegistro(new Date());
            cuenta.setFechaModificacion(new Date());
            cuentaRepository.save(cuenta);
        }

        List<Integer> estatusList = Arrays.asList(
                EstatusPedidoEnum.COBRADO.getValue(),
                EstatusPedidoEnum.CANCELADO.getValue()
        );

        BigDecimal pending = cuentaRepository.sumPending(idCliente, estatusList);
        cuenta.setSaldo(pending);
        cuentaRepository.save(cuenta);

    }



}