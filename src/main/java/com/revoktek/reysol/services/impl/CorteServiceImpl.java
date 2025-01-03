package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.core.utils.MapperUtil;
import com.revoktek.reysol.dto.CorteDTO;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.TipoCorteDTO;
import com.revoktek.reysol.persistence.entities.CalculoSacrificio;
import com.revoktek.reysol.persistence.entities.Corte;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.Sacrificio;
import com.revoktek.reysol.persistence.entities.TipoCorte;
import com.revoktek.reysol.persistence.repositories.CalculoSacrificioRepository;
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

    @Override
    @Transactional
    public void save(CorteDTO corteDTO) throws ServiceLayerException {
        try {

            TipoCorte tipoCorte = tipoCorteRepository.findByIdTipoCorte(corteDTO.getTipoCorte().getIdTipoCorte());
            Producto producto = productoRepository.findByIdProducto(corteDTO.getProducto().getIdProducto());
            Corte corte = corteRepository.findByProductoAndTipoCorte(producto, tipoCorte);
            if (applicationUtil.isNull(corte)) {
                corte = new Corte();
                corte.setCantidad(corteDTO.getCantidad());
                corte.setProducto(producto);
                corte.setTipoCorte(tipoCorte);
            } else {
                corte.setCantidad(corteDTO.getCantidad());
            }
            corteRepository.save(corte);

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

                ProductoDTO productoDTO = ProductoDTO.builder()
                        .idProducto(corte.getProducto().getIdProducto())
                        .nombre(corte.getProducto().getNombre())
                        .descripcion(corte.getProducto().getNombre())
                        .precio(corte.getProducto().getPrecio())
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

            if (applicationUtil.isNull(cantidad) || cantidad <= 0) {
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

                    CalculoSacrificio calculoSacrificio = new CalculoSacrificio();
                    calculoSacrificio.setPendiente(corteDTO.getFaltante());
                    calculoSacrificio.setCantidad(corteDTO.getCantidad());
                    calculoSacrificio.setSacrificio(sacrificio);
                    calculoSacrificio.setTipoCorte(tipoCorte);
                    calculoSacrificio.setProducto(producto);

                    calculoSacrificioRepository.save(calculoSacrificio);

                    return corteDTO;
                }).toList();

            }


            return corteDTOS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }
}