package com.revoktek.reysol.services;

import java.util.List;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.CuentaDTO;

public interface CuentaService {

    CuentaDTO findOrSaveCuentaByCliente(Long idCliente) throws ServiceLayerException;

     List<CuentaDTO> findAllByFilter(String busqueda) throws ServiceLayerException;

     CuentaDTO findById(Long idCliente) throws ServiceLayerException;

    
}
