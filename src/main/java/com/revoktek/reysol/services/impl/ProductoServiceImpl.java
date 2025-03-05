package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.EstatusClienteEnum;
import com.revoktek.reysol.core.enums.TipoClienteEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.PrecioClienteDTO;
import com.revoktek.reysol.dto.ProductoDTO;
import com.revoktek.reysol.dto.UnidadMedidaDTO;
import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Contacto;
import com.revoktek.reysol.persistence.entities.Domicilio;
import com.revoktek.reysol.persistence.entities.PrecioCliente;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.Ruta;
import com.revoktek.reysol.persistence.entities.TipoCliente;
import com.revoktek.reysol.persistence.repositories.PrecioClienteRepository;
import com.revoktek.reysol.persistence.repositories.ProductoRepository;
import com.revoktek.reysol.services.ProductoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ApplicationUtil applicationUtil;
    private final ProductoRepository productoRepository;
    private final PrecioClienteRepository precioClienteRepository;


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

    @Override
    public List<PrecioClienteDTO> findAllByClient(Long idCliente) throws ServiceLayerException {
        try {

            List<PrecioCliente> productos = precioClienteRepository.findAllByCliente(new Cliente(idCliente));

            if (applicationUtil.isEmptyList(productos)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }

            List<PrecioClienteDTO> productosDTO = productos.stream().map(precioCliente -> {
                Producto producto = precioCliente.getProducto();
                ProductoDTO productoDTO = ProductoDTO.builder()
                        .idProducto(producto.getIdProducto())
                        .nombre(producto.getNombre())
                        .descripcion(producto.getNombre())
                        .build();

                PrecioClienteDTO precioClienteDTO = new PrecioClienteDTO();
                precioClienteDTO.setIdPrecioCliente(precioCliente.getIdPrecioCliente());
                precioClienteDTO.setProducto(productoDTO);
                precioClienteDTO.setPrecio(precioCliente.getPrecio());
                precioClienteDTO.setEstatus(precioCliente.getEstatus());
                precioClienteDTO.setFechaRegistro(precioCliente.getFechaRegistro());

                return precioClienteDTO;
            }).toList();

            log.info("{} elementos encontrados.", productosDTO.size());

            return productosDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void saveClientPrice(PrecioClienteDTO precioClienteDTO) throws ServiceLayerException {
        try {

            Cliente cliente = new Cliente(precioClienteDTO.getCliente().getIdCliente());
            Producto producto = new Producto(precioClienteDTO.getProducto().getIdProducto());

            PrecioCliente precioCliente = precioClienteRepository.findByProductoAndClienteAndEstatus(producto, cliente, Boolean.TRUE);

            if (applicationUtil.isNull(precioCliente)) {

                precioCliente = new PrecioCliente();
                precioCliente.setPrecio(precioClienteDTO.getPrecio());
                precioCliente.setFechaRegistro(new Date());
                precioCliente.setEstatus(Boolean.TRUE);
                precioCliente.setProducto(producto);
                precioCliente.setCliente(cliente);
                precioClienteRepository.save(precioCliente);

            } else if (!precioCliente.getPrecio().equals(precioClienteDTO.getPrecio())) {
                precioCliente.setEstatus(Boolean.FALSE);
                precioClienteRepository.save(precioCliente);

                precioCliente = new PrecioCliente();
                precioCliente.setPrecio(precioClienteDTO.getPrecio());
                precioCliente.setFechaRegistro(new Date());
                precioCliente.setEstatus(Boolean.TRUE);
                precioCliente.setProducto(producto);
                precioCliente.setCliente(cliente);
                precioClienteRepository.save(precioCliente);
            }


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }

    }

    @Override
    @Transactional
    public void deleteClientPrice(Long idPrecioCliente) throws ServiceLayerException {
        try {

            PrecioCliente precioCliente = precioClienteRepository.findById(idPrecioCliente)
                    .orElseThrow(() -> new ServiceLayerException("Producto no encontrado"));

            precioCliente.setEstatus(Boolean.FALSE);
            precioClienteRepository.save(precioCliente);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }
}