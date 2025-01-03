package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.PagoDTO;

public interface PagoService {

    void savePayment(PagoDTO pagoDTO, String token) throws ServiceLayerException;
}
