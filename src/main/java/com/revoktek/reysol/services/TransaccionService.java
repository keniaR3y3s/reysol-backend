package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.TransaccionDTO;

import java.util.List;

public interface TransaccionService {

    List<TransaccionDTO> findAllByCuenta(Long idCuenta) throws ServiceLayerException;
}
