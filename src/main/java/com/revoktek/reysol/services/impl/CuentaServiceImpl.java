package com.revoktek.reysol.services.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.revoktek.reysol.services.TransaccionService;
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

    private final TransaccionRepository transaccionRepository;
    private final TransaccionService transaccionService;
    private final PedidoRepository pedidoRepository;
    private final CuentaRepository cuentaRepository;
    private final ApplicationUtil applicationUtil;
    private final JwtService jwtService;


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
            Cuenta cuenta = cuentaRepository.findByCliente(new Cliente(idCliente));
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

            List<TransaccionDTO> transaccionDTOList = transaccionService.findAllByCuenta(cuenta.getIdCuenta());
            cuentaDTO.setTransacciones(transaccionDTOList);

            return cuentaDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

}