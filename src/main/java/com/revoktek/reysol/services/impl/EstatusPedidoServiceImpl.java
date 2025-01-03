package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.MapperUtil;
import com.revoktek.reysol.dto.EstatusPedidoDTO;
import com.revoktek.reysol.persistence.entities.EstatusPedido;
import com.revoktek.reysol.persistence.repositories.EstatusPedidoRepository;
import com.revoktek.reysol.services.EstatusPedidoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class EstatusPedidoServiceImpl implements EstatusPedidoService {

    private final EstatusPedidoRepository estatusPedidoRepository;
    private final MessageProvider messageProvider;
    private final MapperUtil mapperUtil;


    @Override
    public EstatusPedidoDTO findByID(Integer id) throws ServiceLayerException {
        try {

            Optional<EstatusPedido> optional = estatusPedidoRepository.findById(id);
            if (optional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(id));
            }
            EstatusPedido estatusPedido = optional.get();

            return mapperUtil.parseBetweenObject(EstatusPedidoDTO.class, estatusPedido);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }


}