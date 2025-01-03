package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.CuentaDTO;
import com.revoktek.reysol.dto.TransaccionDTO;

public interface CuentaService {


    Long saveChargue(TransaccionDTO transaccionD, String token) throws ServiceLayerException;
    CuentaDTO findOrSaveCuentaByCliente(Long idCliente) throws ServiceLayerException;
}
