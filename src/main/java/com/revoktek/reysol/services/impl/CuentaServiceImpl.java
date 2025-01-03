package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.TipoTransaccionEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.CuentaDTO;
import com.revoktek.reysol.dto.TransaccionDTO;
import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Cuenta;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Pedido;
import com.revoktek.reysol.persistence.entities.TipoTransaccion;
import com.revoktek.reysol.persistence.entities.Transaccion;
import com.revoktek.reysol.persistence.repositories.CuentaRepository;
import com.revoktek.reysol.persistence.repositories.PedidoRepository;
import com.revoktek.reysol.persistence.repositories.TransaccionRepository;
import com.revoktek.reysol.services.CuentaService;
import com.revoktek.reysol.services.JwtService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Slf4j
@Service
@AllArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;
    private JwtService jwtService;
    private ApplicationUtil applicationUtil;
    private PedidoRepository pedidoRepository;


    @Override
    @Transactional
    public Long saveChargue(TransaccionDTO transaccionDTO, String token) throws ServiceLayerException {
        try {

            Empleado empleado;
            if (applicationUtil.isNull(token)
                    || applicationUtil.isNull(transaccionDTO.getEmpleado())
                    || applicationUtil.isNull(transaccionDTO.getEmpleado().getIdEmpleado())
            ) {
                throw new ServiceLayerException("Empleado no seleccionado para el pago");
            } else if (applicationUtil.nonEmpty(token)) {
                empleado = jwtService.getEmpleado(token);
            } else {
                empleado = new Empleado(transaccionDTO.getEmpleado().getIdEmpleado());
            }

            Pedido pedido = pedidoRepository.findByIdPedido(transaccionDTO.getPedido().getIdPedido());
            CuentaDTO cuentaDTO = findOrSaveCuentaByCliente(pedido.getCliente().getIdCliente());

            Transaccion transaccion = new Transaccion();
            transaccion.setMonto(transaccionDTO.getMonto());
            transaccion.setFechaRegistro(new Date());
            transaccion.setCuenta(new Cuenta(cuentaDTO.getIdCuenta()));
            transaccion.setTipoTransaccion(new TipoTransaccion(TipoTransaccionEnum.COBRO.getValue()));
            transaccion.setEmpleado(empleado);
            transaccion.setPedido(pedido);
            transaccionRepository.save(transaccion);


            Cuenta cuenta = cuentaRepository.findByIdCuenta(cuentaDTO.getIdCuenta());
            cuenta.setSaldo(cuenta.getSaldo().subtract(transaccion.getMonto()));
            cuenta.setFechaModificacion(new Date());
            cuentaRepository.save(cuenta);


            return transaccion.getIdTransaccion();


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public CuentaDTO findOrSaveCuentaByCliente(Long idCliente) throws ServiceLayerException {
        try {

            Cliente cliente = new Cliente(idCliente);

            Cuenta cuenta = cuentaRepository.findByCliente(cliente);

            if (applicationUtil.isNull(cuenta)) {
                cuenta = new Cuenta();
                cuenta.setCliente(cliente);
                cuenta.setSaldo(BigDecimal.ZERO);
                cuenta.setFechaRegistro(new Date());
                cuenta.setFechaModificacion(new Date());
                cuentaRepository.save(cuenta);
            }

            return CuentaDTO.builder().
                    saldo(cuenta.getSaldo())
                    .fechaRegistro(cuenta.getFechaRegistro())
                    .fechaModificacion(cuenta.getFechaModificacion()).build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

}