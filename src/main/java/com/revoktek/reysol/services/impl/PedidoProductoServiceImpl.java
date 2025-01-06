package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.EstatusPedidoEnum;
import com.revoktek.reysol.core.enums.TipoInventarioEnum;
import com.revoktek.reysol.core.enums.TipoMovimientoEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.InventarioDTO;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.PedidoProductoDTO;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.UnidadMedidaDTO;
import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Inventario;
import com.revoktek.reysol.persistence.entities.InventarioHistorial;
import com.revoktek.reysol.persistence.entities.Pedido;
import com.revoktek.reysol.persistence.entities.PedidoProducto;
import com.revoktek.reysol.persistence.entities.PrecioCliente;
import com.revoktek.reysol.persistence.entities.PrecioHistorial;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.TipoMovimiento;
import com.revoktek.reysol.persistence.entities.UnidadMedida;
import com.revoktek.reysol.persistence.repositories.InventarioHistorialRepository;
import com.revoktek.reysol.persistence.repositories.InventarioRepository;
import com.revoktek.reysol.persistence.repositories.PedidoProductoRepository;
import com.revoktek.reysol.persistence.repositories.PrecioClienteRepository;
import com.revoktek.reysol.persistence.repositories.PrecioHistorialRepository;
import com.revoktek.reysol.persistence.repositories.ProductoRepository;
import com.revoktek.reysol.services.InventarioService;
import com.revoktek.reysol.services.PedidoProductoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class PedidoProductoServiceImpl implements PedidoProductoService {

    private final InventarioHistorialRepository inventarioHistorialRepository;
    private final PrecioHistorialRepository precioHistorialRepository;
    private final PedidoProductoRepository pedidoProductoRepository;
    private final PrecioClienteRepository precioClienteRepository;
    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    private final MessageProvider messageProvider;
    private final ApplicationUtil applicationUtil;


    @Override
    public List<PedidoProductoDTO> findAllByProducto(Long id) throws ServiceLayerException {
        try {
            log.info("findAllByProducto.id : {}", id);

            List<PedidoProducto> pedidoProductos = pedidoProductoRepository.findAllByPedido(id);
            if (applicationUtil.isEmptyList(pedidoProductos)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }
            log.info("{} elementos encontrados.", pedidoProductos.size());

            List<PedidoProductoDTO> dtoList = pedidoProductos.parallelStream().map(pedidoProducto -> {

                ProductoDTO productoDTO = null;
                Producto producto = pedidoProducto.getProducto();
                if (applicationUtil.nonNull(producto)) {

                    UnidadMedida unidadMedida = producto.getUnidadMedida();
                    UnidadMedidaDTO unidadMedidaDTO = null;

                    if (applicationUtil.nonNull(unidadMedida)) {
                        unidadMedidaDTO = UnidadMedidaDTO.builder()
                                .idUnidadMedida(unidadMedida.getIdUnidadMedida())
                                .nombre(unidadMedida.getNombre())
                                .descripcion(unidadMedida.getDescripcion())
                                .build();
                    }

                    productoDTO = ProductoDTO.builder()
                            .idProducto(producto.getIdProducto())
                            .nombre(producto.getNombre())
                            .descripcion(producto.getNombre())
                            .precio(producto.getPrecio())
                            .unidadMedida(unidadMedidaDTO)
                            .build();
                }


                PedidoProductoDTO pedidoProductoDTO = PedidoProductoDTO.builder()
                        .idPedidoProducto(pedidoProducto.getIdPedidoProducto())
                        .precio(pedidoProducto.getPrecio())
                        .cantidadSolicitada(pedidoProducto.getCantidadSolicitada())
                        .cantidadDespachada(pedidoProducto.getCantidadDespachada())
                        .diferencia(pedidoProducto.getDiferencia())
                        .pesoDespachado(pedidoProducto.getPesoDespachado())
                        .pesoEntregado(pedidoProducto.getPesoEntregado())
                        .producto(productoDTO)
                        .build();


                return pedidoProductoDTO;
            }).toList();

            return dtoList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void saveAllDispatch(List<PedidoProductoDTO> productos, Long idEmpleado) throws ServiceLayerException {
        try {

            if (applicationUtil.isEmptyList(productos)) {
                throw new ServiceLayerException("Los productos son requeridos");
            }

            for (PedidoProductoDTO productoDTO : productos) {

                Optional<PedidoProducto> optional = pedidoProductoRepository.findById(productoDTO.getIdPedidoProducto());
                if (optional.isEmpty()) {
                    throw new ServiceLayerException(messageProvider.getMessageNotFound(productoDTO.getIdPedidoProducto()));
                }

                PedidoProducto pedidoProducto = optional.get();

                Long idProducto = pedidoProducto.getProducto().getIdProducto();
                Integer idTipoInventario = getTipoInventario(productoDTO);


                InventarioDTO inventarioDTO = inventarioService.findOrSaveByProductoAndTipoInventario(idProducto, idTipoInventario, idEmpleado);
                if (inventarioDTO.getCantidad().compareTo(productoDTO.getCantidadDespachada()) < 0) {
                    throw new ServiceLayerException("Solo " + inventarioDTO.getCantidad().toPlainString() + " disponible de " + pedidoProducto.getProducto().getNombre());
                }

                Inventario inventario = inventarioRepository.findByIdInventario(inventarioDTO.getIdInventario());

                pedidoProducto.setCantidadDespachada(productoDTO.getCantidadDespachada());
                pedidoProducto.setPesoDespachado(productoDTO.getPesoDespachado());
                pedidoProducto.setInventario(inventario);
                pedidoProductoRepository.save(pedidoProducto);


                inventario.setCantidad(inventario.getCantidad().subtract(pedidoProducto.getCantidadDespachada()));
                inventario.setPeso(inventario.getPeso().subtract(pedidoProducto.getPesoDespachado()));
                inventario.setFechaModificacion(new Date());
                inventarioRepository.save(inventario);


                InventarioHistorial inventarioHistorial = new InventarioHistorial();
                inventarioHistorial.setInventario(inventario);
                inventarioHistorial.setCantidad(pedidoProducto.getCantidadDespachada());
                inventarioHistorial.setFechaRegistro(new Date());
                inventarioHistorial.setEmpleado(new Empleado(idEmpleado));
                inventarioHistorial.setTipoMovimiento(new TipoMovimiento(TipoMovimientoEnum.SALIDA.getValue()));
                inventarioHistorialRepository.save(inventarioHistorial);

            }


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public BigDecimal saveProductosExtemporaneos(List<PedidoProductoDTO> productos, Long idPedido, Long idEmpleado) throws ServiceLayerException {
        try {

            if (applicationUtil.isEmptyList(productos)) {
                throw new ServiceLayerException("Los productos son requeridos");
            }

            Pedido pedido = new Pedido(idPedido);

            BigDecimal total = BigDecimal.ZERO;

            for (PedidoProductoDTO productoDTO : productos) {

                Long idProducto = productoDTO.getProducto().getIdProducto();
                Integer idTipoInventario = getTipoInventario(productoDTO);

                Producto producto = productoRepository.findByIdProducto(idProducto);

                InventarioDTO inventarioDTO = inventarioService.findOrSaveByProductoAndTipoInventario(idProducto, idTipoInventario, idEmpleado);
                if (inventarioDTO.getCantidad().compareTo(productoDTO.getCantidadDespachada()) < 0) {
                    throw new ServiceLayerException("Solo " + inventarioDTO.getCantidad().toPlainString() + " disponible de " + producto.getNombre());
                }

                Inventario inventario = inventarioRepository.findByIdInventario(inventarioDTO.getIdInventario());


                PedidoProducto pedidoProducto = new PedidoProducto();
                pedidoProducto.setPrecio(producto.getPrecio());
                pedidoProducto.setCantidadSolicitada(productoDTO.getCantidadDespachada());
                pedidoProducto.setCantidadDespachada(productoDTO.getCantidadDespachada());
                pedidoProducto.setDiferencia(BigDecimal.ZERO);
                pedidoProducto.setPesoDespachado(productoDTO.getPesoDespachado());
                pedidoProducto.setPesoEntregado(productoDTO.getPesoDespachado());
                pedidoProducto.setPedido(pedido);
                pedidoProducto.setProducto(producto);
                pedidoProducto.setInventario(inventario);
                pedidoProductoRepository.save(pedidoProducto);

                BigDecimal subtotal = pedidoProducto.getPrecio().multiply(pedidoProducto.getCantidadDespachada());
                total = total.add(subtotal);


                inventario.setCantidad(inventario.getCantidad().subtract(pedidoProducto.getCantidadDespachada()));
                inventario.setPeso(inventario.getPeso().subtract(pedidoProducto.getPesoDespachado()));
                inventario.setFechaModificacion(new Date());
                inventarioRepository.save(inventario);


                InventarioHistorial inventarioHistorial = new InventarioHistorial();
                inventarioHistorial.setInventario(inventario);
                inventarioHistorial.setCantidad(pedidoProducto.getCantidadDespachada());
                inventarioHistorial.setFechaRegistro(new Date());
                inventarioHistorial.setEmpleado(new Empleado(idEmpleado));
                inventarioHistorial.setTipoMovimiento(new TipoMovimiento(TipoMovimientoEnum.SALIDA.getValue()));

                inventarioHistorialRepository.save(inventarioHistorial);

            }


            return total;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void saveAllDeliveryProducts(List<PedidoProductoDTO> productos) throws ServiceLayerException {
        try {

            if (applicationUtil.isEmptyList(productos)) {
                throw new ServiceLayerException("Los productos son requeridos");
            }

            List<PedidoProducto> pedidoProductos = new ArrayList<>();

            for (PedidoProductoDTO productoDTO : productos) {

                Optional<PedidoProducto> optional = pedidoProductoRepository.findById(productoDTO.getIdPedidoProducto());
                if (optional.isEmpty()) {
                    throw new ServiceLayerException(messageProvider.getMessageNotFound(productoDTO.getIdPedidoProducto()));
                }

                PedidoProducto pedidoProducto = optional.get();
                pedidoProducto.setPesoEntregado(productoDTO.getPesoEntregado());
                pedidoProductos.add(pedidoProducto);
            }

            pedidoProductoRepository.saveAll(pedidoProductos);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public BigDecimal getCantidadSolicitadaByProducto(Long idProducto) throws ServiceLayerException {
        try {

            log.info("getCantidadSolicitadaByProducto.idProducto : {}", idProducto);
            Integer statusPedido = EstatusPedidoEnum.PENDIENTE.getValue();
            BigDecimal solicitado = pedidoProductoRepository.sumCantidadsolicitadaByProductoAndEstatusPediddo(new Producto(idProducto), statusPedido);
            return applicationUtil.nonNull(solicitado) ? solicitado : BigDecimal.ZERO;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public BigDecimal saveProductosPedido(PedidoDTO pedidoDTO, Long idEmpleado, Long idCliente) throws ServiceLayerException {
        try {

            List<PedidoProductoDTO> productos = pedidoDTO.getProductos();
            if (applicationUtil.isEmptyList(productos)) {
                throw new ServiceLayerException("Los productos son requeridos");
            }

            Pedido pedido = new Pedido(pedidoDTO.getIdPedido());
            Cliente cliente = new Cliente(idCliente);
            BigDecimal total = BigDecimal.ZERO;
            Date fechaRegistro = new Date();

            for (PedidoProductoDTO productoDTO : productos) {

                Long idProducto = productoDTO.getProducto().getIdProducto();

                Producto producto = productoRepository.findByIdProducto(idProducto);
                PrecioCliente precioCliente = precioClienteRepository.findByProductoAndClienteAndEstatus(producto, cliente, Boolean.TRUE);
                List<PrecioHistorial> precios = precioHistorialRepository.findAllByProductoAndPrecioAndFechaRegistroLessThanEqualOrderByFechaRegistroDesc(producto, producto.getPrecio(), fechaRegistro);


                PedidoProducto pedidoProducto = new PedidoProducto();
                pedidoProducto.setPrecio(producto.getPrecio());
                pedidoProducto.setCantidadSolicitada(productoDTO.getCantidadSolicitada());
                pedidoProducto.setCantidadDespachada(BigDecimal.ZERO);
                pedidoProducto.setDiferencia(BigDecimal.ZERO);
                pedidoProducto.setPesoDespachado(BigDecimal.ZERO);
                pedidoProducto.setPesoEntregado(BigDecimal.ZERO);
                pedidoProducto.setPedido(pedido);
                pedidoProducto.setProducto(producto);
                if (applicationUtil.nonNull(precioCliente)) {
                    pedidoProducto.setPrecioCliente(precioCliente);
                    pedidoProducto.setPrecio(precioCliente.getPrecio());
                }
                if (applicationUtil.nonEmptyList(precios) && applicationUtil.isNull(precioCliente)) {
                    pedidoProducto.setPrecioHistorial(precios.get(0));
                }
                pedidoProductoRepository.save(pedidoProducto);

                BigDecimal subtotal = pedidoProducto.getPrecio().multiply(pedidoProducto.getCantidadDespachada());
                total = total.add(subtotal);

            }


            return total;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    private Integer getTipoInventario(PedidoProductoDTO productoDTO) {
        if (applicationUtil.nonNull(productoDTO.getInventario())
                && applicationUtil.nonNull(productoDTO.getInventario().getTipoInventario())
                && applicationUtil.nonNull(productoDTO.getInventario().getTipoInventario().getIdTipoInventario())
        ) {
            return productoDTO.getInventario().getTipoInventario().getIdTipoInventario();
        }
        return TipoInventarioEnum.FRESCO.getValue();
    }

}