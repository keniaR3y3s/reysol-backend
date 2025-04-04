package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.TemplateDTO;

public interface TemplateService {

    TemplateDTO getTicket(Long idPedido) throws ServiceLayerException;

}
