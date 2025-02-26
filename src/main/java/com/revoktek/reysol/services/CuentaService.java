package com.revoktek.reysol.services;

import java.util.List;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.CuentaDTO;
import com.revoktek.reysol.dto.TransaccionDTO;

public interface CuentaService {


    Long saveChargue(TransaccionDTO transaccionD, String token) throws ServiceLayerException;

    CuentaDTO findOrSaveCuentaByCliente(Long idCliente) throws ServiceLayerException;

     List<CuentaDTO> findAllByFilter(String busqueda) throws ServiceLayerException;

     CuentaDTO findById(Long idCliente) throws ServiceLayerException;

    
}
