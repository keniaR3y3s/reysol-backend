package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.EmpleadoDTO;

import java.util.List;

public interface EmpleadoService {


    EmpleadoDTO findByID(Long id) throws ServiceLayerException;


    List<EmpleadoDTO>  findAllByRol(Long idRol) throws ServiceLayerException;

    List<EmpleadoDTO>  findAllByFilter(String busqueda) throws ServiceLayerException;

    void save(EmpleadoDTO empleadoDTO) throws ServiceLayerException;

    void update(EmpleadoDTO empleadoDTO) throws ServiceLayerException;

}
