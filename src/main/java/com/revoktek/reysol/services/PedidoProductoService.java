package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.ProductoCancelacionDTO;
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

    void cancelPedidoProducto(ProductoCancelacionDTO productoCancelacionDTO) throws ServiceLayerException;

    PedidoDTO findPedidoByPedidoProducto(Long idPedidoProducto) throws ServiceLayerException;

    BigDecimal getTotalPedido(Long idPedido) throws ServiceLayerException;
}
