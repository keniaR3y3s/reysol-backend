package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.EstatusPedidoDTO;

public interface EstatusPedidoService {


    EstatusPedidoDTO findByID(Integer id) throws ServiceLayerException;


}
