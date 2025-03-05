package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.PrecioClienteDTO;
import com.revoktek.reysol.dto.ProductoDTO;

import java.util.List;

public interface ProductoService {

    List<ProductoDTO> findAll() throws ServiceLayerException;

    List<PrecioClienteDTO> findAllByClient(Long idCliente) throws ServiceLayerException ;

    void saveClientPrice(PrecioClienteDTO precioClienteDTO) throws ServiceLayerException;

    void deleteClientPrice(Long idPrecioCliente) throws ServiceLayerException;
}
