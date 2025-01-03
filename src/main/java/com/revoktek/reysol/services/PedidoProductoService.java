package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.PedidoProductoDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PedidoProductoService {

    List<PedidoProductoDTO> findAllByProducto(Long id) throws ServiceLayerException;

    void saveAllDispatch(List<PedidoProductoDTO> productoDTOS, Long idEmpleado) throws ServiceLayerException;

    BigDecimal saveProductosExtemporaneos(List<PedidoProductoDTO> productos, Long idPedido, Long idEmpleado) throws ServiceLayerException;

    void saveAllDeliveryProducts(List<PedidoProductoDTO> productos) throws ServiceLayerException;

    BigDecimal getCantidadSolicitadaByProducto(Long idProducto) throws ServiceLayerException;

    BigDecimal saveProductosPedido(PedidoDTO pedidoDTO, Long idEmpleado, Long idCliente) throws ServiceLayerException;
}
