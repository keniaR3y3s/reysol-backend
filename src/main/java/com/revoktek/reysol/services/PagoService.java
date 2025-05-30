package com.revoktek.reysol.services;

import java.util.List;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.CancelacionPagoDTO;
import com.revoktek.reysol.dto.PagoDTO;
import com.revoktek.reysol.dto.PedidoDTO;

public interface PagoService {

    void savePayment(PagoDTO pagoDTO, String token) throws ServiceLayerException;

    void changeEstatusCancel(CancelacionPagoDTO ct, String token) throws ServiceLayerException;

    void changeEstatusAuthorized(PedidoDTO pedido, String token) throws ServiceLayerException;

    List<PagoDTO> findByPedido(Long idPedido) throws ServiceLayerException;

}
