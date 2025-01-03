package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.UnidadMedidaDTO;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.repositories.ProductoRepository;
import com.revoktek.reysol.services.ProductoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ApplicationUtil applicationUtil;
    private final ProductoRepository productoRepository;


    @Override
    public List<ProductoDTO> findAll() throws ServiceLayerException {
        try {

            List<Producto> productos = productoRepository.findAllByFilter();

            if (applicationUtil.isEmptyList(productos)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }
            log.info("{} elementos encontrados.", productos.size());

            List<ProductoDTO> productosDTO = productos.stream().map(producto -> {

                ProductoDTO productoDTO = ProductoDTO.builder()
                        .idProducto(producto.getIdProducto())
                        .nombre(producto.getNombre())
                        .descripcion(producto.getNombre())
                        .build();

                if (applicationUtil.nonNull(producto.getUnidadMedida())) {
                    UnidadMedidaDTO unidadMedidaDTO = UnidadMedidaDTO.builder()
                            .idUnidadMedida(producto.getUnidadMedida().getIdUnidadMedida())
                            .nombre(producto.getUnidadMedida().getNombre())
                            .descripcion(producto.getUnidadMedida().getDescripcion())
                            .build();
                    productoDTO.setUnidadMedida(unidadMedidaDTO);
                }

                return productoDTO;

            }).toList();

            return productosDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }
}