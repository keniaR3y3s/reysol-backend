package com.revoktek.reysol.services;

import java.util.List;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.PagoDTO;

public interface PagoService {

    void savePayment(PagoDTO pagoDTO, String token) throws ServiceLayerException;

    void changeEstatusCancel(Long idPago) throws ServiceLayerException;

    List<PagoDTO> findByPedido(Long idPedido) throws ServiceLayerException;

}
