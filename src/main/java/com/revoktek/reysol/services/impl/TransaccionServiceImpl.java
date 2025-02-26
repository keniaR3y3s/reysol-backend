package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.EstatusPagoDTO;
import com.revoktek.reysol.dto.FormaPagoDTO;
import com.revoktek.reysol.dto.MetodoPagoDTO;
import com.revoktek.reysol.dto.PagoDTO;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.TipoTransaccionDTO;
import com.revoktek.reysol.dto.TransaccionDTO;
import com.revoktek.reysol.dto.UnidadMedidaDTO;
import com.revoktek.reysol.persistence.entities.Cuenta;
import com.revoktek.reysol.persistence.entities.EstatusPago;
import com.revoktek.reysol.persistence.entities.FormaPago;
import com.revoktek.reysol.persistence.entities.MetodoPago;
import com.revoktek.reysol.persistence.entities.Pago;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.TipoTransaccion;
import com.revoktek.reysol.persistence.entities.Transaccion;
import com.revoktek.reysol.persistence.repositories.ProductoRepository;
import com.revoktek.reysol.persistence.repositories.TransaccionRepository;
import com.revoktek.reysol.services.ProductoService;
import com.revoktek.reysol.services.TransaccionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final ApplicationUtil applicationUtil;



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

                TransaccionDTO transaccionDTO = TransaccionDTO.builder()
                        .idTransaccion(transaccion.getIdTransaccion())
                        .fechaRegistro(transaccion.getFechaRegistro())
                        .monto(transaccion.getMonto())
                        .tipoTransaccion(tipoTransaccionDTO)
                        .pago(pagoDTO)
                        .build();

                return transaccionDTO;

            }).toList();

            return transaccionDTOList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }
}