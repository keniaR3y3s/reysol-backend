package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.EstatusPedidoEnum;
import com.revoktek.reysol.core.enums.TipoInventarioEnum;
import com.revoktek.reysol.core.enums.TipoMovimientoEnum;
import com.revoktek.reysol.core.enums.TipoPrecioEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.CorteDTO;
import com.revoktek.reysol.dto.EmpleadoDTO;
import com.revoktek.reysol.dto.ProductoCancelacionDTO;
import com.revoktek.reysol.dto.InventarioDTO;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.PedidoProductoDTO;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.TipoCorteDTO;
import com.revoktek.reysol.dto.TipoInventarioDTO;
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
import com.revoktek.reysol.persistence.entities.TipoCorte;
import com.revoktek.reysol.persistence.entities.TipoInventario;
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
import java.util.Objects;
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

            List<PedidoProductoDTO> dtoList = pedidoProductos.stream().map(pedidoProducto -> {

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
                Corte corte = pedidoProducto.getCorte();
                CorteDTO corteDTO = null;
                if(applicationUtil.nonNull(corte)) {
                    corteDTO = new CorteDTO();
                    corteDTO.setIdCorte(corte.getIdCorte());
                    corteDTO.setCantidad(corte.getCantidad());
                    corteDTO.setPrecioPieza(corte.getPrecioPieza());
                    corteDTO.setPrecioKilo(corte.getPrecioKilo());
                    corteDTO.setProducto(productoDTO);

                    TipoCorte tipoCorte = corte.getTipoCorte();
                    TipoCorteDTO tipoCorteDTO = new TipoCorteDTO();
                    tipoCorteDTO.setIdTipoCorte(tipoCorte.getIdTipoCorte());
                    tipoCorteDTO.setNombre(tipoCorte.getNombre());
                    tipoCorteDTO.setDescripcion(tipoCorte.getDescripcion());

                    corteDTO.setTipoCorte(tipoCorteDTO);
                }
                Inventario inventario = pedidoProducto.getInventario();
                InventarioDTO  inventarioDTO = null;
                if(applicationUtil.nonNull(inventario)) {
                    inventarioDTO = new InventarioDTO();
                    inventarioDTO.setIdInventario(inventario.getIdInventario());
                    inventarioDTO.setCantidad(inventario.getCantidad());
                    inventarioDTO.setPeso(inventario.getPeso());
                    inventarioDTO.setFechaModificacion(inventario.getFechaModificacion());
                    inventarioDTO.setProducto(productoDTO);

                    TipoInventario tipoInventario = inventario.getTipoInventario();
                    TipoInventarioDTO tipoInventarioDTO = new TipoInventarioDTO();
                    tipoInventarioDTO.setIdTipoInventario(tipoInventario.getIdTipoInventario());
                    tipoInventarioDTO.setNombre(tipoInventario.getNombre());
                    tipoInventarioDTO.setDescripcion(tipoInventario.getDescripcion());
                    tipoInventarioDTO.setEstatus(tipoInventario.getEstatus());

                    inventarioDTO.setTipoInventario(tipoInventarioDTO);
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
                        .subtotal(pedidoProducto.getSubtotal())
                        .diferencia(pedidoProducto.getDiferencia())
                        .tipoPrecio(pedidoProducto.getTipoPrecio())
                        .inventario(inventarioDTO)
                        .producto(productoDTO)
                        .corte(corteDTO)
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
    public BigDecimal saveAllDispatch(List<PedidoProductoDTO> productos, Long idEmpleado) throws ServiceLayerException {
        try {

            if (applicationUtil.isEmptyList(productos)) {
                throw new ServiceLayerException("Los productos son requeridos");
            }

            BigDecimal total = BigDecimal.ZERO;

            for (PedidoProductoDTO productoDTO : productos) {

                Optional<PedidoProducto> optional = pedidoProductoRepository.findById(productoDTO.getIdPedidoProducto());
                if (optional.isEmpty()) {
                    throw new ServiceLayerException(messageProvider.getMessageNotFound(productoDTO.getIdPedidoProducto()));
                }

                PedidoProducto pedidoProducto = optional.get();

                Long idProducto = pedidoProducto.getProducto().getIdProducto();
                Integer idTipoInventario = getTipoInventario(productoDTO);


                InventarioDTO inventarioDTO = inventarioService.findOrSaveByProductoAndTipoInventario(idProducto, idTipoInventario, idEmpleado);
                Inventario inventario = inventarioRepository.findByIdInventario(inventarioDTO.getIdInventario());
                /*if (inventarioDTO.getCantidad().compareTo(productoDTO.getCantidadDespachada()) < 0) {
                    throw new ServiceLayerException("Solo " + inventarioDTO.getCantidad().toPlainString() + " disponible de " + pedidoProducto.getProducto().getNombre());
                }*/

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
                inventarioHistorial.setPeso(pedidoProducto.getPesoDespachado());
                inventarioHistorial.setFechaRegistro(new Date());
                inventarioHistorial.setEmpleado(new Empleado(idEmpleado));
                inventarioHistorial.setTipoMovimiento(new TipoMovimiento(TipoMovimientoEnum.SALIDA.getValue()));
                inventarioHistorialRepository.save(inventarioHistorial);

                BigDecimal subtotal = BigDecimal.ZERO;

                if(pedidoProducto.getTipoPrecio().equals(TipoPrecioEnum.PIEZA.getValue())){
                    subtotal =  pedidoProducto.getPrecio().multiply(pedidoProducto.getCantidadDespachada());
                }else if(pedidoProducto.getTipoPrecio().equals(TipoPrecioEnum.KILO.getValue())){
                    subtotal = pedidoProducto.getPrecio().multiply(pedidoProducto.getPesoDespachado());
                }
                pedidoProducto.setSubtotal(subtotal);

                pedidoProductoRepository.save(pedidoProducto);

                total = total.add(subtotal);

            }

            return total;

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
                pedidoProducto.setCantidadSolicitada(productoDTO.getCantidadSolicitada());
                pedidoProducto.setCantidadDespachada(productoDTO.getCantidadSolicitada());
                pedidoProducto.setCantidadEntregada(productoDTO.getCantidadSolicitada());
                pedidoProducto.setDiferencia(BigDecimal.ZERO);
                pedidoProducto.setPesoSolicitado(productoDTO.getPesoSolicitado());
                pedidoProducto.setPesoDespachado(productoDTO.getPesoSolicitado());
                pedidoProducto.setPesoEntregado(productoDTO.getPesoSolicitado());
                pedidoProducto.setPedido(pedido);
                pedidoProducto.setProducto(producto);
                pedidoProducto.setInventario(inventario);
                pedidoProducto.setCorte(corte);
                pedidoProducto.setCorteHistorial(corteHistorial.orElse(null));
                pedidoProducto.setTipoPrecio(productoDTO.getTipoPrecio());

                BigDecimal subtotal = BigDecimal.ZERO;
                BigDecimal precio = BigDecimal.ZERO;

                if(pedidoProducto.getTipoPrecio().equals(TipoPrecioEnum.PIEZA.getValue())){
                    precio = corte.getPrecioPieza();
                    subtotal = precio.multiply(pedidoProducto.getCantidadDespachada());
                }else if(pedidoProducto.getTipoPrecio().equals(TipoPrecioEnum.KILO.getValue())){
                    precio = corte.getPrecioKilo();
                    subtotal = precio.multiply(pedidoProducto.getPesoDespachado());
                }

                pedidoProducto.setPrecio(precio);
                pedidoProducto.setSubtotal(subtotal);

                pedidoProductoRepository.save(pedidoProducto);

                total = total.add(subtotal);


                inventario.setCantidad(inventario.getCantidad().subtract(pedidoProducto.getCantidadDespachada()));
                inventario.setPeso(inventario.getPeso().subtract(pedidoProducto.getPesoDespachado()));
                inventario.setFechaModificacion(new Date());
                inventarioRepository.save(inventario);


                InventarioHistorial inventarioHistorial = new InventarioHistorial();
                inventarioHistorial.setInventario(inventario);
                inventarioHistorial.setCantidad(pedidoProducto.getCantidadDespachada());
                inventarioHistorial.setPeso(pedidoProducto.getPesoDespachado());
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

            for (PedidoProductoDTO productoDTO : productos) {
                Long idCorte = productoDTO.getCorte().getIdCorte();
                Corte corte = corteRepository.findById(idCorte).orElseThrow(() -> new ServiceLayerException(messageProvider.getMessageNotFound(idCorte)));
                TipoCorte tipoCorte = corte.getTipoCorte();
                Producto producto = corte.getProducto();

                PrecioCliente precioCliente = precioClienteRepository.findByProductoAndClienteAndEstatusAndTipoCorte(producto, cliente, Boolean.TRUE, tipoCorte);
                Optional<CorteHistorial> corteHistorial = corteHistorialRepository.findAllByCorteOrderByFechaRegistroDesc(corte).stream().findFirst();


                Integer idTipoInventario = getTipoInventario(productoDTO);

                InventarioDTO inventarioDTO = inventarioService.findOrSaveByProductoAndTipoInventario(producto.getIdProducto(), idTipoInventario, idEmpleado);
                Inventario inventario = inventarioRepository.findByIdInventario(inventarioDTO.getIdInventario());


                PedidoProducto pedidoProducto = new PedidoProducto();
                pedidoProducto.setEstatus(Boolean.TRUE);
                pedidoProducto.setSubtotal(BigDecimal.ZERO);
                pedidoProducto.setCantidadSolicitada(productoDTO.getCantidadSolicitada());
                pedidoProducto.setPesoSolicitado(productoDTO.getPesoSolicitado());
                pedidoProducto.setCantidadDespachada(BigDecimal.ZERO);
                pedidoProducto.setPesoDespachado(BigDecimal.ZERO);
                pedidoProducto.setCantidadEntregada(BigDecimal.ZERO);
                pedidoProducto.setPesoEntregado(BigDecimal.ZERO);
                pedidoProducto.setPedido(pedido);
                pedidoProducto.setProducto(producto);
                pedidoProducto.setInventario(inventario);
                pedidoProducto.setCorteHistorial(corteHistorial.orElse(null));
                pedidoProducto.setCorte(corte);
                pedidoProducto.setEstatus(Boolean.TRUE);

                pedidoProducto.setTipoPrecio(productoDTO.getTipoPrecio());
                BigDecimal subtotal = BigDecimal.ZERO;
                BigDecimal precio = BigDecimal.ZERO;

                if (applicationUtil.nonNull(precioCliente)) {
                    pedidoProducto.setPrecioCliente(precioCliente);
                    if(pedidoProducto.getTipoPrecio().equals(TipoPrecioEnum.PIEZA.getValue())){
                        precio = precioCliente.getPrecioPieza();
                        subtotal = precio.multiply(pedidoProducto.getCantidadSolicitada());
                    }else if(pedidoProducto.getTipoPrecio().equals(TipoPrecioEnum.KILO.getValue())){
                        precio = precioCliente.getPrecioKilo();
                        subtotal = precio.multiply(pedidoProducto.getPesoSolicitado());
                    }
                }else{
                    if(pedidoProducto.getTipoPrecio().equals(TipoPrecioEnum.PIEZA.getValue())){
                        precio = corte.getPrecioPieza();
                        subtotal = precio.multiply(pedidoProducto.getCantidadSolicitada());
                    }else if(pedidoProducto.getTipoPrecio().equals(TipoPrecioEnum.KILO.getValue())){
                        precio = corte.getPrecioKilo();
                        subtotal = precio.multiply(pedidoProducto.getPesoSolicitado());
                    }
                }
                pedidoProducto.setPrecio(precio);
                pedidoProducto.setSubtotal(subtotal);

                pedidoProductoRepository.save(pedidoProducto);

                total = total.add(subtotal);


            }


            return total;
        } catch (ServiceLayerException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException("Error al guardar el producto del pedido.");
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