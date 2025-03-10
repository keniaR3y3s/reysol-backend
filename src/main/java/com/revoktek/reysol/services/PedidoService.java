package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.filter.FilterPedidoDTO;

import java.util.List;

public interface PedidoService {

    List<PedidoDTO> findAllByFilter(FilterPedidoDTO filterPedido) throws ServiceLayerException;

    PedidoDTO findByID(Long id) throws ServiceLayerException;

    void saveDispatch(PedidoDTO pedidoDTO, String token) throws ServiceLayerException;

    void saveExtemporaneo(PedidoDTO pedidoDTO, String token) throws ServiceLayerException;

    void saveEmpleadoEntrega(PedidoDTO pedidoDTO) throws ServiceLayerException;

    void removeEmpleadoEntrega(PedidoDTO pedidoDTO) throws ServiceLayerException;

    void saveDelivery(PedidoDTO pedidoDTO, String token) throws ServiceLayerException;

    void save(PedidoDTO pedidoDTO, String token) throws ServiceLayerException;

    List<PedidoDTO> findByCliente(Long idCliente) throws ServiceLayerException;
}
