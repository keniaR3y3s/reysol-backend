package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.TipoInventarioEnum;
import com.revoktek.reysol.core.enums.TipoMovimientoEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.InventarioDTO;
import com.revoktek.reysol.dto.InventarioHistorialDTO;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.TipoInventarioDTO;
import com.revoktek.reysol.dto.TipoMovimientoDTO;
import com.revoktek.reysol.dto.UnidadMedidaDTO;
import com.revoktek.reysol.dto.filter.FilterDTO;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Inventario;
import com.revoktek.reysol.persistence.entities.InventarioHistorial;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.TipoInventario;
import com.revoktek.reysol.persistence.entities.TipoMovimiento;
import com.revoktek.reysol.persistence.repositories.InventarioHistorialRepository;
import com.revoktek.reysol.persistence.repositories.InventarioRepository;
import com.revoktek.reysol.persistence.repositories.ProductoRepository;
import com.revoktek.reysol.persistence.repositories.TipoInventarioRepository;
import com.revoktek.reysol.services.InventarioService;
import com.revoktek.reysol.services.JwtService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@AllArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final JwtService jwtService;
    private final ApplicationUtil applicationUtil;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final TipoInventarioRepository tipoInventarioRepository;
    private final InventarioHistorialRepository inventarioHistorialRepository;


    @Override
    @Transactional
    public InventarioDTO findOrSaveByProductoAndTipoInventario(Long idProducto, Integer idTipoInventatio, Long idEmpleado) throws ServiceLayerException {
        try {

            Producto producto = productoRepository.findByIdProducto(idProducto);
            TipoInventario tipoInventario = tipoInventarioRepository.findByIdTipoInventario(idTipoInventatio);
            Inventario inventario = inventarioRepository.findByProductoAndTipoInventario(producto, tipoInventario);

            if (applicationUtil.isNull(inventario)) {
                inventario = new Inventario();
                inventario.setCantidad(BigDecimal.ZERO);
                inventario.setPeso(BigDecimal.ZERO);
                inventario.setFechaRegistro(new Date());
                inventario.setProducto(producto);
                inventario.setTipoInventario(tipoInventario);
                inventario.setFechaModificacion(inventario.getFechaRegistro());
                inventarioRepository.save(inventario);

            }

            return InventarioDTO.builder().
                    idInventario(inventario.getIdInventario())
                    .cantidad(inventario.getCantidad())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public List<InventarioDTO> findAllByFilter(FilterDTO filterDTO) throws ServiceLayerException {
        try {

            filterDTO = applicationUtil.isNull(filterDTO) ? new FilterDTO() : filterDTO;

            log.info("findAllByFiltro.filterPedido : {}", filterDTO);

            List<Inventario> inventarios = inventarioRepository.findAllByFilter(
                    filterDTO.getFechaInicio(),
                    filterDTO.getFechaFin(),
                    filterDTO.getBusqueda()
            );

            if (applicationUtil.isEmptyList(inventarios)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }
            log.info("{} elementos encontrados.", inventarios.size());

            List<InventarioDTO> inventarioDTOS = inventarios.stream().map(inventario -> {

                InventarioDTO inventarioDTO = InventarioDTO.builder()
                        .idInventario(inventario.getIdInventario())
                        .cantidad(inventario.getCantidad())
                        .fechaRegistro(inventario.getFechaRegistro())
                        .fechaModificacion(inventario.getFechaModificacion())
                        .build();

                Producto producto = inventario.getProducto();

                ProductoDTO productoDTO = ProductoDTO.builder()
                        .idProducto(producto.getIdProducto())
                        .nombre(producto.getNombre())
                        .descripcion(producto.getNombre())
                        .precio(producto.getPrecio())
                        .build();

                inventarioDTO.setProducto(productoDTO);


                if (applicationUtil.nonNull(producto.getUnidadMedida())) {
                    UnidadMedidaDTO unidadMedidaDTO = UnidadMedidaDTO.builder()
                            .idUnidadMedida(producto.getUnidadMedida().getIdUnidadMedida())
                            .nombre(producto.getUnidadMedida().getNombre())
                            .descripcion(producto.getUnidadMedida().getDescripcion())
                            .build();
                    productoDTO.setUnidadMedida(unidadMedidaDTO);
                }

                if (applicationUtil.nonNull(inventario.getTipoInventario())) {
                    TipoInventarioDTO tipoInventarioDTO = TipoInventarioDTO.builder()
                            .idTipoInventario(inventario.getTipoInventario().getIdTipoInventario())
                            .nombre(inventario.getTipoInventario().getNombre())
                            .descripcion(inventario.getTipoInventario().getDescripcion())
                            .build();
                    inventarioDTO.setTipoInventario(tipoInventarioDTO);
                }


                return inventarioDTO;
            }).toList();

            return inventarioDTOS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public List<InventarioHistorialDTO> findAllMovements(FilterDTO filterDTO) throws ServiceLayerException {
        try {
            log.info("findAllByFiltro.filterPedido : {}", filterDTO);

            List<InventarioHistorial> inventarios = inventarioHistorialRepository.findAllByFilter(
                    filterDTO.getFechaInicio(),
                    filterDTO.getFechaFin(),
                    filterDTO.getBusqueda()
            );

            if (applicationUtil.isEmptyList(inventarios)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }
            log.info("{} elementos encontrados.", inventarios.size());

            List<InventarioHistorialDTO> inventarioDTOS = inventarios.stream().map(inventarioHistorial -> {

                InventarioHistorialDTO inventarioHistorialDTO = new InventarioHistorialDTO();
                inventarioHistorialDTO.setIdInventarioHistorial(inventarioHistorial.getIdInventarioHistorial());
                inventarioHistorialDTO.setCantidad(inventarioHistorial.getCantidad());
                inventarioHistorialDTO.setPeso(inventarioHistorial.getPeso());
                inventarioHistorialDTO.setFechaRegistro(inventarioHistorial.getFechaRegistro());

                TipoMovimiento tipoMovimiento = inventarioHistorial.getTipoMovimiento();

                TipoMovimientoDTO tipoMovimientoDTO = new TipoMovimientoDTO();
                tipoMovimientoDTO.setIdTipoMovimiento(tipoMovimiento.getIdTipoMovimiento());
                tipoMovimientoDTO.setNombre(tipoMovimiento.getNombre());

                inventarioHistorialDTO.setTipoMovimiento(tipoMovimientoDTO);


                Inventario inventario = inventarioHistorial.getInventario();

                InventarioDTO inventarioDTO = InventarioDTO.builder()
                        .idInventario(inventario.getIdInventario())
                        .build();

                inventarioHistorialDTO.setInventario(inventarioDTO);

                Producto producto = inventario.getProducto();

                ProductoDTO productoDTO = ProductoDTO.builder()
                        .idProducto(producto.getIdProducto())
                        .nombre(producto.getNombre())
                        .descripcion(producto.getNombre())
                        .build();

                inventarioDTO.setProducto(productoDTO);


                if (applicationUtil.nonNull(producto.getUnidadMedida())) {
                    UnidadMedidaDTO unidadMedidaDTO = UnidadMedidaDTO.builder()
                            .idUnidadMedida(producto.getUnidadMedida().getIdUnidadMedida())
                            .nombre(producto.getUnidadMedida().getNombre())
                            .descripcion(producto.getUnidadMedida().getDescripcion())
                            .build();
                    productoDTO.setUnidadMedida(unidadMedidaDTO);
                }


                return inventarioHistorialDTO;
            }).toList();

            return inventarioDTOS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void save(InventarioDTO inventarioDTO, String token) throws ServiceLayerException {
        try {

            Date now = new Date();
            ProductoDTO productoDTO = inventarioDTO.getProducto();
            Long idProducto = productoDTO.getIdProducto();

            Integer idTipoInventario = TipoInventarioEnum.FRESCO.getValue();
            if (applicationUtil.isNull(inventarioDTO.getTipoInventario())
                    && applicationUtil.isNull(inventarioDTO.getTipoInventario().getIdTipoInventario())
            ) {
                idTipoInventario = inventarioDTO.getTipoInventario().getIdTipoInventario();
            }

            Integer idTipoMovimiento = TipoMovimientoEnum.ENTRADA.getValue();
            if (applicationUtil.isNull(inventarioDTO.getTipoMovimiento())
                    || applicationUtil.isNull(inventarioDTO.getTipoMovimiento().getIdTipoMovimiento())
            ) {
                idTipoMovimiento = inventarioDTO.getTipoMovimiento().getIdTipoMovimiento();
            }

            Empleado empleado = jwtService.getEmpleado(token);

            InventarioDTO inventarioSavedDTO = this.findOrSaveByProductoAndTipoInventario(
                    idProducto,
                    idTipoInventario,
                    empleado.getIdEmpleado()
            );
            if (applicationUtil.isNull(inventarioSavedDTO)) {
                inventarioSavedDTO = this.findOrSaveByProductoAndTipoInventario(
                        idProducto,
                        (Objects.equals(idTipoInventario, TipoInventarioEnum.FRESCO.getValue()) ?
                                TipoInventarioEnum.REFRIGERADO.getValue() : TipoInventarioEnum.FRESCO.getValue()),
                        empleado.getIdEmpleado()
                );
            }

            Inventario inventario = inventarioRepository.findByIdInventario(inventarioSavedDTO.getIdInventario());
            inventario.setFechaModificacion(now);
            if (Objects.equals(idTipoMovimiento, TipoMovimientoEnum.ENTRADA.getValue())) {
                inventario.setCantidad(inventario.getCantidad().add(inventarioDTO.getCantidad()));
                inventario.setPeso(inventario.getCantidad().add(inventarioDTO.getPeso()));
                inventarioRepository.save(inventario);


                InventarioHistorial inventarioHistorial = new InventarioHistorial();
                inventarioHistorial.setInventario(inventario);
                inventarioHistorial.setCantidad(inventarioDTO.getCantidad());
                inventarioHistorial.setPeso(inventarioDTO.getPeso());
                inventarioHistorial.setFechaRegistro(now);
                inventarioHistorial.setEmpleado(empleado);
                inventarioHistorial.setTipoMovimiento(new TipoMovimiento(idTipoMovimiento));
                inventarioHistorialRepository.save(inventarioHistorial);
            } else {
                inventario.setCantidad(inventario.getCantidad().subtract(inventarioDTO.getCantidad()));
                inventario.setPeso(inventario.getCantidad().subtract(inventarioDTO.getPeso()));
                inventarioRepository.save(inventario);

                InventarioHistorial inventarioHistorial = new InventarioHistorial();
                inventarioHistorial.setInventario(inventario);
                inventarioHistorial.setCantidad(inventarioDTO.getCantidad());
                inventarioHistorial.setPeso(inventarioDTO.getPeso());
                inventarioHistorial.setFechaRegistro(now);
                inventarioHistorial.setEmpleado(empleado);
                inventarioHistorial.setTipoMovimiento(new TipoMovimiento(idTipoMovimiento));

                inventarioHistorialRepository.save(inventarioHistorial);
            }


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }
}