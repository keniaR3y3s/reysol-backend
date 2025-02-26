package com.revoktek.reysol.services;

import java.util.List;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.ClienteDTO;

public interface ClienteService {


    List<ClienteDTO> findAllByFilter(String busqueda) throws ServiceLayerException;

    ClienteDTO findById(Long idCliente) throws ServiceLayerException;

    void changeEstatus(Long idCliente) throws ServiceLayerException;

    void save(ClienteDTO clienteDTO) throws ServiceLayerException;

    void update(ClienteDTO clienteDTO) throws ServiceLayerException;

    ClienteDTO saveExtemporaneo() throws ServiceLayerException;

}
