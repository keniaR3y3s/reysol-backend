package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.CorteDTO;
import com.revoktek.reysol.dto.TipoCorteDTO;

import java.util.List;


public interface CorteService {

    void save(CorteDTO corteDTO, String token) throws ServiceLayerException;

    TipoCorteDTO findById(Integer idTipoCorte) throws ServiceLayerException;

    List<CorteDTO> calculate(Integer idTipoCorte, Integer cantidad, Boolean almacen, String token) throws ServiceLayerException;

    List<TipoCorteDTO> findAllWithProducts() throws ServiceLayerException;
}
