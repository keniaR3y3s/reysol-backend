package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.InventarioDTO;
import com.revoktek.reysol.dto.InventarioHistorialDTO;
import com.revoktek.reysol.dto.filter.FilterDTO;

import java.util.List;

public interface InventarioService {


    InventarioDTO findOrSaveByProductoAndTipoInventario(Long idProducto, Integer idTipoInventatio, Long idEmpleado) throws ServiceLayerException;

    List<InventarioDTO> findAllByFilter(FilterDTO filterDTO) throws ServiceLayerException;

    List<InventarioHistorialDTO> findAllMovements(FilterDTO filterDTO) throws ServiceLayerException;

    void save(InventarioDTO inventarioDTO, String token) throws ServiceLayerException;
}
