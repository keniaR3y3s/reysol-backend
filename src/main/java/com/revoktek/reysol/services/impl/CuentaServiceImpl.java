package com.revoktek.reysol.services.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.revoktek.reysol.core.enums.TipoTransaccionEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.ClienteDTO;
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
            CuentaDTO cuentaDTO = findOrSaveCuentaByCliente(pedido.getCliente().getIdCliente());

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
    @Transactional
    public CuentaDTO findOrSaveCuentaByCliente(Long idCliente) throws ServiceLayerException {
        try {
            // 1. Crear una referencia al cliente
            Cliente cliente = new Cliente(idCliente);
    
            // 2. Buscar la cuenta del cliente en la base de datos
            Cuenta cuenta = cuentaRepository.findByCliente(cliente);
    
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
                Cuenta cuenta = cuentaRepository.findById(idCliente)
                        .orElseThrow(() -> new ServiceLayerException("Cuenta no encontrada"));

                return CuentaDTO.builder()
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
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ServiceLayerException(e);
            }
}

}