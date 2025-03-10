package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.core.utils.MapperUtil;
import com.revoktek.reysol.dto.CorteDTO;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.TipoCorteDTO;
import com.revoktek.reysol.dto.UnidadMedidaDTO;
import com.revoktek.reysol.persistence.entities.CalculoSacrificio;
import com.revoktek.reysol.persistence.entities.Corte;
import com.revoktek.reysol.persistence.entities.CorteHistorial;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.Sacrificio;
import com.revoktek.reysol.persistence.entities.TipoCorte;
import com.revoktek.reysol.persistence.repositories.CalculoSacrificioRepository;
import com.revoktek.reysol.persistence.repositories.CorteHistorialRepository;
import com.revoktek.reysol.persistence.repositories.CorteRepository;
import com.revoktek.reysol.persistence.repositories.InventarioRepository;
import com.revoktek.reysol.persistence.repositories.ProductoRepository;
import com.revoktek.reysol.persistence.repositories.SacrificioRepository;
import com.revoktek.reysol.persistence.repositories.TipoCorteRepository;
import com.revoktek.reysol.services.CorteService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class CorteServiceImpl implements CorteService {

    private final PedidoProductoServiceImpl pedidoProductoServiceImpl;
    private final InventarioRepository inventarioRepository;
    private final TipoCorteRepository tipoCorteRepository;
    private final ProductoRepository productoRepository;
    private final CorteRepository corteRepository;
    private final ApplicationUtil applicationUtil;
    private final MessageProvider messageProvider;
    private final MapperUtil mapperUtil;
    private final JwtServiceImpl jwtServiceImpl;
    private final SacrificioRepository sacrificioRepository;
    private final CalculoSacrificioRepository calculoSacrificioRepository;
    private final CorteHistorialRepository corteHistorialRepository;

    @Override
    @Transactional
    public void save(CorteDTO corteDTO, String token) throws ServiceLayerException {
        try {

            TipoCorte tipoCorte = tipoCorteRepository.findByIdTipoCorte(corteDTO.getTipoCorte().getIdTipoCorte());
            Producto producto = productoRepository.findByIdProducto(corteDTO.getProducto().getIdProducto());
            Corte corte = corteRepository.findByProductoAndTipoCorte(producto, tipoCorte);

            if (applicationUtil.isNull(corte)) {
                corte = new Corte();
                corte.setProducto(producto);
                corte.setTipoCorte(tipoCorte);
            }
            corte.setEstatus(Boolean.TRUE);
            corte.setCantidad(corteDTO.getCantidad());
            corte.setPrecio(corteDTO.getPrecio());
            corteRepository.save(corte);

            Empleado empleado = jwtServiceImpl.getEmpleado(token);

            CorteHistorial corteHistorial = new CorteHistorial();
            corteHistorial.setPrecio(corteDTO.getPrecio());
            corteHistorial.setCantidad(corteDTO.getCantidad());
            corteHistorial.setFechaRegistro(new Date());
            corteHistorial.setCorte(corte);
            corteHistorial.setEmpleado(empleado);
            corteHistorialRepository.save(corteHistorial);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public TipoCorteDTO findById(Integer idTipoCorte) throws ServiceLayerException {
        try {

            Optional<TipoCorte> optional = tipoCorteRepository.findById(idTipoCorte);
            if (optional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(idTipoCorte));
            }

            TipoCorte tipoCorte = optional.get();

            TipoCorteDTO tipoCorteDTO = new TipoCorteDTO();
            tipoCorteDTO.setIdTipoCorte(idTipoCorte);
            tipoCorteDTO.setNombre(tipoCorte.getNombre());
            tipoCorteDTO.setDescripcion(tipoCorte.getDescripcion());

            List<Corte> cortes = corteRepository.findAllByTipoCorte(tipoCorte);
            List<CorteDTO> corteDTOList = cortes.stream().map(corte -> {

                UnidadMedidaDTO unidadMedidaDTO = mapperUtil.parseBetweenObject(UnidadMedidaDTO.class, corte.getProducto().getUnidadMedida());

                ProductoDTO productoDTO = ProductoDTO.builder()
                        .idProducto(corte.getProducto().getIdProducto())
                        .nombre(corte.getProducto().getNombre())
                        .descripcion(corte.getProducto().getNombre())
                        .unidadMedida(unidadMedidaDTO)
                        .build();

                CorteDTO corteDTO = new CorteDTO();
                corteDTO.setCantidad(corte.getCantidad());
                corteDTO.setProducto(productoDTO);
                return corteDTO;

            }).toList();

            tipoCorteDTO.setCortes(corteDTOList);

            return tipoCorteDTO;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public List<CorteDTO> calculate(Integer idTipoCorte, Integer cantidad, Boolean almacen, String token) throws ServiceLayerException {
        try {
            System.out.println("idTipoCorte: " + idTipoCorte);
            System.out.println("cantidad: " + cantidad);
            System.out.println("almacen: " + almacen);
            if (applicationUtil.isNull(cantidad)) {
                throw new ServiceLayerException("Ingrese una cantidad valida");
            }

            TipoCorteDTO tipoCorteDTO = findById(idTipoCorte);
            List<CorteDTO> corteDTOS = tipoCorteDTO.getCortes();
            BigDecimal cantidadBigDecimal = new BigDecimal(cantidad);
            TipoCorte tipoCorte = new TipoCorte(idTipoCorte);

            if (applicationUtil.nonEmptyList(corteDTOS)) {

                Empleado empleado = jwtServiceImpl.getEmpleado(token);

                Sacrificio sacrificio = new Sacrificio();
                sacrificio.setFechaRegistro(new Date());
                sacrificio.setEmpleado(empleado);
                sacrificioRepository.save(sacrificio);

                corteDTOS = corteDTOS.stream().map(corteDTO -> {

                    Producto producto = new Producto(corteDTO.getProducto().getIdProducto());
                    BigDecimal solicitado = pedidoProductoServiceImpl.getCantidadSolicitadaByProducto(producto.getIdProducto());

                    BigDecimal disponible = corteDTO.getCantidad().multiply(cantidadBigDecimal);
                    if (applicationUtil.nonNull(almacen) && almacen) {
                        disponible = disponible.add(inventarioRepository.findCantidadByProducto(producto));
                    }
                    corteDTO.setCantidad(disponible);

                    if (solicitado.compareTo(disponible) > 0) {
                        corteDTO.setFaltante(solicitado.subtract(disponible));
                    } else {
                        corteDTO.setFaltante(BigDecimal.ZERO);
                    }

//                    CalculoSacrificio calculoSacrificio = new CalculoSacrificio();
//                    calculoSacrificio.setPendiente(corteDTO.getFaltante());
//                    calculoSacrificio.setCantidad(corteDTO.getCantidad());
//                    calculoSacrificio.setSacrificio(sacrificio);
//                    calculoSacrificio.setTipoCorte(tipoCorte);
//                    calculoSacrificio.setProducto(producto);
//
//                    calculoSacrificioRepository.save(calculoSacrificio);

                    return corteDTO;
                }).toList();

            }


            return corteDTOS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public List<TipoCorteDTO> findAllWithProducts() throws ServiceLayerException {
        try {

            List<TipoCorte> tipoCortes = tipoCorteRepository.findAll();
            List<TipoCorteDTO> tipoCorteDTOS = tipoCortes.stream().map(tipoCorte -> {
                TipoCorteDTO tipoCorteDTO = new TipoCorteDTO();
                tipoCorteDTO.setIdTipoCorte(tipoCorte.getIdTipoCorte());
                tipoCorteDTO.setNombre(tipoCorte.getNombre());
                tipoCorteDTO.setDescripcion(tipoCorte.getDescripcion());

                List<Corte> cortes = corteRepository.findAllByTipoCorte(tipoCorte);
                List<CorteDTO> corteDTOList = cortes.stream().map(corte -> {

                    UnidadMedidaDTO unidadMedidaDTO = mapperUtil.parseBetweenObject(UnidadMedidaDTO.class, corte.getProducto().getUnidadMedida());

                    ProductoDTO productoDTO = ProductoDTO.builder()
                            .idProducto(corte.getProducto().getIdProducto())
                            .nombre(corte.getProducto().getNombre())
                            .descripcion(corte.getProducto().getNombre())
                            .unidadMedida(unidadMedidaDTO)
                            .build();

                    CorteDTO corteDTO = new CorteDTO();
                    corteDTO.setIdCorte(corte.getIdCorte());
                    corteDTO.setCantidad(corte.getCantidad());
                    corteDTO.setPrecio(corte.getPrecio());
                    corteDTO.setProducto(productoDTO);
                    return corteDTO;

                }).toList();

                tipoCorteDTO.setCortes(corteDTOList);

                return tipoCorteDTO;
            }).toList();

            return tipoCorteDTOS;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public void delete(CorteDTO corteDTO, String token) throws ServiceLayerException {
        try {

            Corte corte = corteRepository.findById(corteDTO.getIdCorte()).orElseThrow(() -> new ServiceLayerException("El corte no existe"));
            corte.setEstatus(Boolean.FALSE);
            corteRepository.save(corte);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }
}