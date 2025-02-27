package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.TipoTransaccionEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.ClienteDTO;
import com.revoktek.reysol.dto.CuentaDTO;
import com.revoktek.reysol.dto.EmpleadoDTO;
import com.revoktek.reysol.dto.EstatusPagoDTO;
import com.revoktek.reysol.dto.EstatusPedidoDTO;
import com.revoktek.reysol.dto.FormaPagoDTO;
import com.revoktek.reysol.dto.MetodoPagoDTO;
import com.revoktek.reysol.dto.PagoDTO;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.TipoTransaccionDTO;
import com.revoktek.reysol.dto.TransaccionDTO;
import com.revoktek.reysol.dto.UnidadMedidaDTO;
import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Cuenta;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.EstatusPago;
import com.revoktek.reysol.persistence.entities.EstatusPedido;
import com.revoktek.reysol.persistence.entities.FormaPago;
import com.revoktek.reysol.persistence.entities.MetodoPago;
import com.revoktek.reysol.persistence.entities.Pago;
import com.revoktek.reysol.persistence.entities.Pedido;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.TipoTransaccion;
import com.revoktek.reysol.persistence.entities.Transaccion;
import com.revoktek.reysol.persistence.repositories.CuentaRepository;
import com.revoktek.reysol.persistence.repositories.PedidoRepository;
import com.revoktek.reysol.persistence.repositories.ProductoRepository;
import com.revoktek.reysol.persistence.repositories.TransaccionRepository;
import com.revoktek.reysol.services.CuentaService;
import com.revoktek.reysol.services.JwtService;
import com.revoktek.reysol.services.ProductoService;
import com.revoktek.reysol.services.TransaccionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final PedidoRepository pedidoRepository;
    private final CuentaRepository cuentaRepository;
    private final ApplicationUtil applicationUtil;
    private final CuentaService cuentaService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public Long saveChargue(TransaccionDTO transaccionDTO, String token) throws ServiceLayerException {
        try {
            // 1. Validar que el token y el empleado sean válidos
            Empleado empleado;
            if (applicationUtil.isNull(token)
                    || applicationUtil.isNull(transaccionDTO.getEmpleado())
                    || applicationUtil.isNull(transaccionDTO.getEmpleado().getIdEmpleado())
            ) {
                throw new ServiceLayerException("Empleado no seleccionado para el pago");
            } else if (applicationUtil.nonEmpty(token)) {
                // Si hay un token válido, obtener el empleado a partir del token
                empleado = jwtService.getEmpleado(token);
            } else {
                // Si no hay token, crear un nuevo objeto Empleado con el ID proporcionado
                empleado = new Empleado(transaccionDTO.getEmpleado().getIdEmpleado());
            }

            // 2. Obtener el pedido asociado a la transacción
            Pedido pedido = pedidoRepository.findByIdPedido(transaccionDTO.getPedido().getIdPedido());

            // 3. Obtener o crear la cuenta del cliente
            CuentaDTO cuentaDTO = cuentaService.findOrSaveCuentaByCliente(pedido.getCliente().getIdCliente());

            // 4. Crear una nueva transacción con los datos recibidos
            Transaccion transaccion = new Transaccion();
            transaccion.setMonto(transaccionDTO.getMonto()); // Establecer el monto
            transaccion.setFechaRegistro(new Date()); // Registrar la fecha actual
            transaccion.setCuenta(new Cuenta(cuentaDTO.getIdCuenta())); // Asignar la cuenta
            transaccion.setTipoTransaccion(new TipoTransaccion(TipoTransaccionEnum.COBRO.getValue())); // Definir el tipo de transacción (Cobro)
            transaccion.setEmpleado(empleado); // Asociar el empleado
            transaccion.setPedido(pedido); // Asociar el pedido
            transaccionRepository.save(transaccion); // Guardar la transacción en la base de datos

            // 5. Actualizar el saldo de la cuenta del cliente
            Cuenta cuenta = cuentaRepository.findByIdCuenta(cuentaDTO.getIdCuenta());
            cuenta.setSaldo(cuenta.getSaldo().subtract(transaccion.getMonto())); // Restar el monto cobrado
            cuenta.setFechaModificacion(new Date()); // Actualizar la fecha de modificación
            cuentaRepository.save(cuenta); // Guardar los cambios en la cuenta

            return transaccion.getIdTransaccion(); // Retornar el ID de la transacción creada

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e); // Manejo de excepciones
        }
    }

    @Override
    public List<TransaccionDTO> findAllByCuenta(Long idCuenta) throws ServiceLayerException {
        try {

            List<Transaccion> transaccions = transaccionRepository.findAllByCuenta(new Cuenta(idCuenta));

            if (applicationUtil.isEmptyList(transaccions)) {
                log.info("Sin transacciones  encontrados.");
                return Collections.emptyList();
            }
            log.info("{} elementos encontrados.", transaccions.size());

            List<TransaccionDTO> transaccionDTOList = transaccions.stream().map(transaccion -> {

                TipoTransaccion tipoTransaccion = transaccion.getTipoTransaccion();
                TipoTransaccionDTO tipoTransaccionDTO = new TipoTransaccionDTO(tipoTransaccion.getIdTipoTransaccion(), tipoTransaccion.getNombre());

                Pago pago = transaccion.getPago();
                PagoDTO pagoDTO = null;
                if (applicationUtil.nonNull(pago)) {
                    EstatusPago estatusPago = pago.getEstatusPago();
                    MetodoPago metodoPago = pago.getMetodoPago();
                    FormaPago formaPago = pago.getFormaPago();

                    EstatusPagoDTO estatusPagoDTO = new EstatusPagoDTO(estatusPago.getIdEstatusPago(), estatusPago.getNombre());
                    MetodoPagoDTO metodoPagoDTO = new MetodoPagoDTO(metodoPago.getIdMetodoPago(), metodoPago.getNombre());
                    FormaPagoDTO formaPagoDTO = new FormaPagoDTO(formaPago.getIdFormaPago(), formaPago.getNombre(), null, null);

                    pagoDTO = PagoDTO.builder()
                            .idPago(pago.getIdPago())
                            .monto(pago.getMonto())
                            .fechaRegistro(pago.getFechaRegistro())
                            .estatusPago(estatusPagoDTO)
                            .metodoPago(metodoPagoDTO)
                            .formaPago(formaPagoDTO)
                            .build();
                }

                return TransaccionDTO.builder()
                        .idTransaccion(transaccion.getIdTransaccion())
                        .fechaRegistro(transaccion.getFechaRegistro())
                        .monto(transaccion.getMonto())
                        .tipoTransaccion(tipoTransaccionDTO)
                        .pago(pagoDTO)
                        .build();

            }).toList();

            return transaccionDTOList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public TransaccionDTO findById(Long idTransaccion) throws ServiceLayerException {
        try {

            Transaccion transaccion = transaccionRepository.findById(idTransaccion).orElseThrow(() -> new ServiceLayerException("No se encontro el transaccion"));

            TipoTransaccion tipoTransaccion = transaccion.getTipoTransaccion();
            TipoTransaccionDTO tipoTransaccionDTO = new TipoTransaccionDTO(tipoTransaccion.getIdTipoTransaccion(), tipoTransaccion.getNombre());

            Empleado empleado = transaccion.getEmpleado();
            Pedido pedido = transaccion.getPedido();
            Pago pago = transaccion.getPago();

            EmpleadoDTO empleadoDTO = null;
            PedidoDTO pedidoDTO = null;
            PagoDTO pagoDTO = null;

            if (applicationUtil.nonNull(empleado)) {
                empleadoDTO = new EmpleadoDTO(empleado.getIdEmpleado());
                empleadoDTO.setNombre(empleado.getNombre());
                empleadoDTO.setPrimerApellido(empleado.getPrimerApellido());
                empleadoDTO.setSegundoApellido(empleado.getSegundoApellido());
            }

            if (applicationUtil.nonNull(pedido)) {
                pedidoDTO = PedidoDTO.builder()
                        .idPedido(pedido.getIdPedido())
                        .clave(pedido.getClave())
                        .total(pedido.getTotal())
                        .fechaRegistro(pedido.getFechaRegistro())
                        .fechaEntrega(pedido.getFechaEntrega())
                        .fechaDespacha(pedido.getFechaDespacha())
                        .fechaSolicitud(pedido.getFechaSolicitud())
                        .build();
                EstatusPedido estatusPedido = pedido.getEstatusPedido();
                if (applicationUtil.nonNull(estatusPedido)) {
                    EstatusPedidoDTO estatusPedidoDTO = EstatusPedidoDTO.builder()
                            .idEstatusPedido(estatusPedido.getIdEstatusPedido())
                            .nombre(estatusPedido.getNombre())
                            .descripcion(estatusPedido.getDescripcion())
                            .build();
                    pedidoDTO.setEstatusPedido(estatusPedidoDTO);
                }
            }

            if (applicationUtil.nonNull(pago)) {
                EstatusPago estatusPago = pago.getEstatusPago();
                MetodoPago metodoPago = pago.getMetodoPago();
                FormaPago formaPago = pago.getFormaPago();

                EstatusPagoDTO estatusPagoDTO = new EstatusPagoDTO(estatusPago.getIdEstatusPago(), estatusPago.getNombre());
                MetodoPagoDTO metodoPagoDTO = new MetodoPagoDTO(metodoPago.getIdMetodoPago(), metodoPago.getNombre());
                FormaPagoDTO formaPagoDTO = new FormaPagoDTO(formaPago.getIdFormaPago(), formaPago.getNombre(), null, null);

                pagoDTO = PagoDTO.builder()
                        .idPago(pago.getIdPago())
                        .monto(pago.getMonto())
                        .fechaRegistro(pago.getFechaRegistro())
                        .estatusPago(estatusPagoDTO)
                        .metodoPago(metodoPagoDTO)
                        .formaPago(formaPagoDTO)
                        .build();
            }

            return TransaccionDTO.builder()
                    .idTransaccion(transaccion.getIdTransaccion())
                    .fechaRegistro(transaccion.getFechaRegistro())
                    .monto(transaccion.getMonto())
                    .tipoTransaccion(tipoTransaccionDTO)
                    .empleado(empleadoDTO)
                    .pago(pagoDTO)
                    .pedido(pedidoDTO)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }
}