package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.EstatusPedidoEnum;
import com.revoktek.reysol.core.enums.TipoInventarioEnum;
import com.revoktek.reysol.core.enums.TipoMovimientoEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.EmpleadoDTO;
import com.revoktek.reysol.dto.ProductoCancelacionDTO;
import com.revoktek.reysol.dto.InventarioDTO;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.PedidoProductoDTO;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.UnidadMedidaDTO;
import com.revoktek.reysol.persistence.entities.ProductoCancelacion;
import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Corte;
import com.revoktek.reysol.persistence.entities.CorteHistorial;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Inventario;
import com.revoktek.reysol.persistence.entities.InventarioHistorial;
import com.revoktek.reysol.persistence.entities.Pedido;
import com.revoktek.reysol.persistence.entities.PedidoProducto;
import com.revoktek.reysol.persistence.entities.PrecioCliente;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.TipoMovimiento;
import com.revoktek.reysol.persistence.entities.UnidadMedida;
import com.revoktek.reysol.persistence.repositories.CorteRepository;
import com.revoktek.reysol.persistence.repositories.InventarioHistorialRepository;
import com.revoktek.reysol.persistence.repositories.InventarioRepository;
import com.revoktek.reysol.persistence.repositories.PedidoProductoRepository;
import com.revoktek.reysol.persistence.repositories.PrecioClienteRepository;
import com.revoktek.reysol.persistence.repositories.CorteHistorialRepository;
import com.revoktek.reysol.persistence.repositories.ProductoCancelacionRepository;
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
    private final ProductoCancelacionRepository productoCancelacionRepository;
    private final PedidoProductoRepository pedidoProductoRepository;
    private final CorteHistorialRepository corteHistorialRepository;
    private final PrecioClienteRepository precioClienteRepository;
    private final InventarioRepository inventarioRepository;
    private final InventarioService inventarioService;
    private final MessageProvider messageProvider;
    private final ApplicationUtil applicationUtil;
    private final CorteRepository corteRepository;


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
                            .unidadMedida(unidadMedidaDTO)
                            .build();
                }
                ProductoCancelacionDTO productoCancelacionDTO = null;
                if (applicationUtil.nonNull(pedidoProducto.getProductoCancelacion())) {
                    ProductoCancelacion pc = pedidoProducto.getProductoCancelacion();
                    Empleado empleado = pc.getEmpleado();
                    EmpleadoDTO empleadoDTO = EmpleadoDTO.builder().idEmpleado(empleado.getIdEmpleado())
                            .nombre(empleado.getNombre())
                            .primerApellido(empleado.getPrimerApellido())
                            .segundoApellido(empleado.getSegundoApellido())
                            .build();

                    productoCancelacionDTO = ProductoCancelacionDTO.builder()
                            .idProductoCancelacion(pc.getIdProductoCancelacion())
                            .motivo(pc.getMotivo())
                            .fechaRegistro(pc.getFechaRegistro())
                            .empleado(empleadoDTO)
                            .build();
                }


                return PedidoProductoDTO.builder()
                        .idPedidoProducto(pedidoProducto.getIdPedidoProducto())
                        .precio(pedidoProducto.getPrecio())
                        .estatus(pedidoProducto.getEstatus())
                        .productoCancelacion(productoCancelacionDTO)
                        .cantidadSolicitada(pedidoProducto.getCantidadSolicitada())
                        .cantidadDespachada(pedidoProducto.getCantidadDespachada())
                        .cantidadEntregada(pedidoProducto.getCantidadEntregada())
                        .pesoSolicitado(pedidoProducto.getPesoSolicitado())
                        .pesoDespachado(pedidoProducto.getPesoDespachado())
                        .pesoEntregado(pedidoProducto.getPesoEntregado())
                        .cantidadFrias(pedidoProducto.getCantidadFrias())
                        .subtotal(pedidoProducto.getSubtotal())
                        .diferencia(pedidoProducto.getDiferencia())
                        .producto(productoDTO)
                        .build();

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
                /*if (inventarioDTO.getCantidad().compareTo(productoDTO.getCantidadDespachada()) < 0) {
                    throw new ServiceLayerException("Solo " + inventarioDTO.getCantidad().toPlainString() + " disponible de " + pedidoProducto.getProducto().getNombre());
                }*/


                Inventario inventario = inventarioRepository.findByIdInventario(inventarioDTO.getIdInventario());

                pedidoProducto.setCantidadDespachada(productoDTO.getCantidadDespachada());
                pedidoProducto.setPesoDespachado(productoDTO.getPesoDespachado());
                pedidoProducto.setInventario(inventario);
                pedidoProducto.setDiferencia(pedidoProducto.getPesoSolicitado().subtract(pedidoProducto.getPesoDespachado()));
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

                Long idCorte = productoDTO.getCorte().getIdCorte();
                Integer idTipoInventario = getTipoInventario(productoDTO);

                Corte corte = corteRepository.findById(idCorte).orElseThrow(() -> new ServiceLayerException(messageProvider.getMessageNotFound(idCorte)));
                Producto producto = corte.getProducto();

                InventarioDTO inventarioDTO = inventarioService.findOrSaveByProductoAndTipoInventario(producto.getIdProducto(), idTipoInventario, idEmpleado);
//                if (inventarioDTO.getCantidad().compareTo(productoDTO.getCantidadDespachada()) < 0) {
//                    throw new ServiceLayerException("Solo " + inventarioDTO.getCantidad().toPlainString() + " disponible de " + producto.getNombre());
//                }

                Inventario inventario = inventarioRepository.findByIdInventario(inventarioDTO.getIdInventario());

                Optional<CorteHistorial> corteHistorial = corteHistorialRepository.findAllByCorteOrderByFechaRegistroDesc(corte).stream().findFirst();

                PedidoProducto pedidoProducto = new PedidoProducto();
                pedidoProducto.setEstatus(Boolean.TRUE);
                pedidoProducto.setPrecio(corte.getPrecio());
                pedidoProducto.setCantidadSolicitada(productoDTO.getCantidadDespachada());
                pedidoProducto.setCantidadDespachada(productoDTO.getCantidadDespachada());
                pedidoProducto.setDiferencia(BigDecimal.ZERO);
                pedidoProducto.setPesoDespachado(productoDTO.getPesoDespachado());
                pedidoProducto.setPesoEntregado(productoDTO.getPesoDespachado());
                pedidoProducto.setPedido(pedido);
                pedidoProducto.setProducto(producto);
                pedidoProducto.setInventario(inventario);
                pedidoProducto.setCorte(corte);
                pedidoProducto.setCorteHistorial(corteHistorial.orElse(null));
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
                pedidoProducto.setCantidadEntregada(productoDTO.getCantidadEntregada());
                pedidoProducto.setDiferencia(pedidoProducto.getPesoDespachado().subtract(pedidoProducto.getPesoEntregado()));
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
                Long idCorte = productoDTO.getCorte().getIdCorte();
                Corte corte = corteRepository.findById(idCorte).orElseThrow(() -> new ServiceLayerException(messageProvider.getMessageNotFound(idCorte)));

                Producto producto = corte.getProducto();

                PrecioCliente precioCliente = precioClienteRepository.findByProductoAndClienteAndEstatus(producto, cliente, Boolean.TRUE);
                Optional<CorteHistorial> corteHistorial = corteHistorialRepository.findAllByCorteOrderByFechaRegistroDesc(corte).stream().findFirst();


                PedidoProducto pedidoProducto = new PedidoProducto();
                pedidoProducto.setEstatus(Boolean.TRUE);
                pedidoProducto.setPrecio(corte.getPrecio());
                pedidoProducto.setSubtotal(BigDecimal.ZERO);
                pedidoProducto.setCantidadFrias(productoDTO.getCantidadFrias());
                pedidoProducto.setCantidadSolicitada(productoDTO.getCantidadSolicitada());
                pedidoProducto.setPesoSolicitado(productoDTO.getPesoSolicitado());
                pedidoProducto.setCantidadDespachada(BigDecimal.ZERO);
                pedidoProducto.setPesoDespachado(BigDecimal.ZERO);
                pedidoProducto.setCantidadEntregada(BigDecimal.ZERO);
                pedidoProducto.setPesoEntregado(BigDecimal.ZERO);
                pedidoProducto.setPedido(pedido);
                pedidoProducto.setProducto(producto);
                pedidoProducto.setCorteHistorial(corteHistorial.orElse(null));
                pedidoProducto.setEstatus(Boolean.TRUE);

                if (applicationUtil.nonNull(precioCliente)) {
                    pedidoProducto.setPrecioCliente(precioCliente);
                    pedidoProducto.setPrecio(precioCliente.getPrecio());
                }


                pedidoProducto.setSubtotal(pedidoProducto.getPrecio().multiply(pedidoProducto.getPesoSolicitado()));

                pedidoProductoRepository.save(pedidoProducto);


                total = total.add(pedidoProducto.getSubtotal());

            }


            return total;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public void cancelPedidoProducto(ProductoCancelacionDTO productoCancelacionDTO) throws ServiceLayerException {
        try {

            PedidoProductoDTO pedidoProductoDTO = productoCancelacionDTO.getPedidoProducto();

            Optional<PedidoProducto> optional = pedidoProductoRepository.findById(pedidoProductoDTO.getIdPedidoProducto());
            if (optional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(pedidoProductoDTO.getIdPedidoProducto()));
            }
            Empleado empleado = new Empleado(productoCancelacionDTO.getEmpleado().getIdEmpleado());

            PedidoProducto pedidoProducto = optional.get();
            pedidoProducto.setEstatus(Boolean.FALSE);
            pedidoProductoRepository.save(pedidoProducto);

            ProductoCancelacion productoCancelacion = productoCancelacionRepository.findByPedidoProducto(optional.get());
            if (applicationUtil.isNull(productoCancelacion)) {
                productoCancelacion = new ProductoCancelacion();
            }
            productoCancelacion.setMotivo(productoCancelacionDTO.getMotivo());
            productoCancelacion.setFechaRegistro(new Date());
            productoCancelacion.setPedidoProducto(pedidoProducto);
            productoCancelacion.setEmpleado(empleado);
            productoCancelacionRepository.save(productoCancelacion);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public PedidoDTO findPedidoByPedidoProducto(Long idPedidoProducto) throws ServiceLayerException {
        try {

            Optional<PedidoProducto> optional = pedidoProductoRepository.findById(idPedidoProducto);
            if (optional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(idPedidoProducto));
            }
            Pedido pedido = optional.get().getPedido();
            PedidoDTO pedidoDTO = new PedidoDTO();
            pedidoDTO.setIdPedido(pedido.getIdPedido());
            pedidoDTO.setTotal(pedido.getTotal());
            pedidoDTO.setFechaRegistro(pedido.getFechaRegistro());

            return pedidoDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public BigDecimal getTotalPedido(Long idPedido) throws ServiceLayerException {
        try {

            BigDecimal total = pedidoProductoRepository.getTotalPedido(idPedido, Boolean.TRUE);
            return applicationUtil.nonNull(total) ? total : BigDecimal.ZERO;

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