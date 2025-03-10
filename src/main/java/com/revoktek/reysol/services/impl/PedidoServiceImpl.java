package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.EstatusPedidoEnum;
import com.revoktek.reysol.core.enums.TipoClienteEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.core.utils.MapperUtil;
import com.revoktek.reysol.dto.ClienteDTO;
import com.revoktek.reysol.dto.DomicilioDTO;
import com.revoktek.reysol.dto.EmpleadoDTO;
import com.revoktek.reysol.dto.EstatusPedidoDTO;
import com.revoktek.reysol.dto.MetodoPagoDTO;
import com.revoktek.reysol.dto.PagoDTO;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.PedidoProductoDTO;
import com.revoktek.reysol.dto.RutaDTO;
import com.revoktek.reysol.dto.filter.FilterPedidoDTO;
import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Domicilio;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.EstatusPedido;
import com.revoktek.reysol.persistence.entities.MetodoPago;
import com.revoktek.reysol.persistence.entities.Pedido;
import com.revoktek.reysol.persistence.entities.Ruta;
import com.revoktek.reysol.persistence.repositories.ClienteRepository;
import com.revoktek.reysol.persistence.repositories.DomicilioRepository;
import com.revoktek.reysol.persistence.repositories.PedidoRepository;
import com.revoktek.reysol.services.ClienteService;
import com.revoktek.reysol.services.CuentaService;
import com.revoktek.reysol.services.JwtService;
import com.revoktek.reysol.services.PagoService;
import com.revoktek.reysol.services.PedidoProductoService;
import com.revoktek.reysol.services.PedidoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoProductoService pedidoProductoService;
    private final DomicilioRepository domicilioRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteServiceImpl;
    private final PedidoRepository pedidoRepository;
    private final ApplicationUtil applicationUtil;
    private final MessageProvider messageProvider;
    private final MapperUtil mapperUtil;
    private final JwtService jwtService;
    private final PagoService pagoService;
    private final CuentaService cuentaService;


    @Override
    public List<PedidoDTO> findAllByFilter(FilterPedidoDTO filterPedido) throws ServiceLayerException {
        try {

            filterPedido = applicationUtil.isNull(filterPedido) ? new FilterPedidoDTO() : filterPedido;

            log.info("findAllByFiltro.filterPedido : {}", filterPedido);

            List<Integer> estatusList = (applicationUtil.isEmptyList(filterPedido.getEstatusList()) ? null : filterPedido.getEstatusList());

            List<Pedido> pedidos = pedidoRepository.findAllByFilter(
                    filterPedido.getFechaInicio(),
                    filterPedido.getFechaFin(),
                    filterPedido.getIdRuta(),
                    estatusList,
                    filterPedido.getIdEmpleadoEntrega(),
                    filterPedido.getBusqueda(),
                    filterPedido.getIdTipoCliente()
            );
            if (applicationUtil.isEmptyList(pedidos)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }
            log.info("{} elementos encontrados.", pedidos.size());

            List<PedidoDTO> pedidoDTOList = pedidos.stream().map(pedido -> {
                PedidoDTO pedidoDTO = PedidoDTO.builder()
                        .idPedido(pedido.getIdPedido())
                        .clave(pedido.getClave())
                        .fechaEntrega(pedido.getFechaEntrega())
                        .fechaSolicitud(pedido.getFechaSolicitud())
                        .total(pedido.getTotal())
                        .build();
                Cliente cliente = pedido.getCliente();
                if (applicationUtil.nonNull(cliente)) {
                    ClienteDTO clienteDTO = ClienteDTO.builder()
                            .idCliente(cliente.getIdCliente())
                            .alias(cliente.getAlias())
                            .build();
                    pedidoDTO.setCliente(clienteDTO);
                }
                EstatusPedido estatusPedido = pedido.getEstatusPedido();
                if (applicationUtil.nonNull(estatusPedido)) {
                    EstatusPedidoDTO estatusPedidoDTO = EstatusPedidoDTO.builder()
                            .idEstatusPedido(estatusPedido.getIdEstatusPedido())
                            .nombre(estatusPedido.getNombre())
                            .descripcion(estatusPedido.getDescripcion())
                            .build();
                    pedidoDTO.setEstatusPedido(estatusPedidoDTO);
                }
                return pedidoDTO;
            }).toList();

            return pedidoDTOList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }


    @Override
    public PedidoDTO findByID(Long id) throws ServiceLayerException {
        try {

            Optional<Pedido> pedidoOptional = pedidoRepository.findById(id);
            if (pedidoOptional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(id));
            }
            Pedido pedido = pedidoOptional.get();

            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .idPedido(pedido.getIdPedido())
                    .clave(pedido.getClave())
                    .fechaRegistro(pedido.getFechaRegistro())
                    .fechaSolicitud(pedido.getFechaSolicitud())
                    .fechaEntrega(pedido.getFechaEntrega())
                    .fechaDespacha(pedido.getFechaDespacha())
                    .abonado(pedido.getAbonado())
                    .total(pedido.getTotal())
                    .pendiente(pedido.getPendiente())
                    .build();

            MetodoPago metodoPago = pedido.getMetodoPago();
            if (applicationUtil.nonNull(metodoPago)) {
                MetodoPagoDTO metodoPagoDTO = mapperUtil.parseBetweenObject(MetodoPagoDTO.class, metodoPago);
                pedidoDTO.setMetodoPago(metodoPagoDTO);
            }

            Empleado empleado = pedido.getEmpleadoEntrega();
            if (applicationUtil.nonNull(empleado)) {
                EmpleadoDTO empleadoDTO = new EmpleadoDTO();
                empleadoDTO.setIdEmpleado(empleado.getIdEmpleado());
                empleadoDTO.setNombre(empleado.getNombre());
                empleadoDTO.setPrimerApellido(empleado.getPrimerApellido());
                empleadoDTO.setSegundoApellido(empleado.getSegundoApellido());
                pedidoDTO.setEmpleadoEntrega(empleadoDTO);
            }

            empleado = pedido.getEmpleadoDespacha();
            if (applicationUtil.nonNull(empleado)) {
                EmpleadoDTO empleadoDTO = new EmpleadoDTO();
                empleadoDTO.setIdEmpleado(empleado.getIdEmpleado());
                empleadoDTO.setNombre(empleado.getNombre());
                empleadoDTO.setPrimerApellido(empleado.getPrimerApellido());
                empleadoDTO.setSegundoApellido(empleado.getSegundoApellido());
                pedidoDTO.setEmpleadoDespacha(empleadoDTO);
            }

            Cliente cliente = pedido.getCliente();
            if (applicationUtil.nonNull(cliente)) {
                ClienteDTO clienteDTO = ClienteDTO.builder()
                        .idCliente(cliente.getIdCliente())
                        .alias(cliente.getAlias())
                        .build();
                pedidoDTO.setCliente(clienteDTO);

                Ruta ruta = cliente.getRuta();
                if (applicationUtil.nonNull(ruta)) {
                    RutaDTO rutaDTO = mapperUtil.parseBetweenObject(RutaDTO.class, ruta);
                    clienteDTO.setRuta(rutaDTO);
                }
            }

            Ruta ruta = pedido.getRuta();
            if (applicationUtil.nonNull(ruta)) {
                pedidoDTO.setRuta(mapperUtil.parseBetweenObject(RutaDTO.class, ruta));
            }

            Domicilio domicilio = pedido.getDomicilio();
            if (applicationUtil.nonNull(domicilio)) {
                domicilio.setCliente(null);
                DomicilioDTO domicilioDTO = mapperUtil.parseBetweenObject(DomicilioDTO.class, domicilio);
                pedidoDTO.setDomicilio(domicilioDTO);
            }

            EstatusPedido estatusPedido = pedido.getEstatusPedido();
            if (applicationUtil.nonNull(estatusPedido)) {
                EstatusPedidoDTO estatusPedidoDTO = EstatusPedidoDTO.builder()
                        .idEstatusPedido(estatusPedido.getIdEstatusPedido())
                        .nombre(estatusPedido.getNombre())
                        .descripcion(estatusPedido.getDescripcion())
                        .build();
                pedidoDTO.setEstatusPedido(estatusPedidoDTO);
            }

            List<PedidoProductoDTO> productos = pedidoProductoService.findAllByProducto(pedido.getIdPedido());
            pedidoDTO.setProductos(productos);

            List<PagoDTO> pagoDTOS = pagoService.findByPedido(id);
            pedidoDTO.setPagos(pagoDTOS);

            return pedidoDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void saveDispatch(PedidoDTO pedidoDTO, String token) throws ServiceLayerException {
        try {

            Optional<Pedido> pedidoOptional = pedidoRepository.findById(pedidoDTO.getIdPedido());
            if (pedidoOptional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(pedidoDTO.getIdPedido()));
            }

            Empleado empleadoDespacha = jwtService.getEmpleado(token);
            EstatusPedido estatusDespachado = new EstatusPedido(EstatusPedidoEnum.DESPACHADO.getValue());

            Pedido pedido = pedidoOptional.get();
            pedido.setFechaDespacha(new Date());
            pedido.setEmpleadoDespacha(empleadoDespacha);
            pedido.setEstatusPedidoPrevio(new EstatusPedido(pedido.getEstatusPedido().getIdEstatusPedido()));
            pedido.setEstatusPedido(estatusDespachado);

            pedidoRepository.save(pedido);

            pedidoProductoService.saveAllDispatch(pedidoDTO.getProductos(), empleadoDespacha.getIdEmpleado());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void saveExtemporaneo(PedidoDTO pedidoDTO, String token) throws ServiceLayerException {
        try {

            Date now = new Date();
            String prefix = applicationUtil.getPrefixPedido(TipoClienteEnum.EXTEMPORANEO.getValue());

            Empleado empleado = jwtService.getEmpleado(token);

            EstatusPedido estatusEntregado = new EstatusPedido(EstatusPedidoEnum.ENTREGADO.getValue());

            ClienteDTO clienteDTO = clienteServiceImpl.saveExtemporaneo();
            Cliente cliente = new Cliente(clienteDTO.getIdCliente());


            Pedido pedido = new Pedido();
            pedido.setClave(System.currentTimeMillis() + "");
            pedido.setFechaRegistro(now);
            pedido.setFechaSolicitud(now);
            pedido.setFechaDespacha(now);
            pedido.setFechaEntrega(now);
            pedido.setTotal(BigDecimal.ZERO);
            pedido.setAbonado(BigDecimal.ZERO);
            pedido.setPendiente(BigDecimal.ZERO);
            pedido.setCliente(cliente);
            pedido.setEstatusPedidoPrevio(estatusEntregado);
            pedido.setEstatusPedido(estatusEntregado);
            pedido.setEmpleadoDespacha(empleado);
            pedido.setEmpleadoEntrega(empleado);
            pedidoRepository.save(pedido);

            pedido.setClave(prefix + pedido.getIdPedido());

            BigDecimal total = pedidoProductoService.saveProductosExtemporaneos(pedidoDTO.getProductos(), pedido.getIdPedido(), empleado.getIdEmpleado());
            pedido.setTotal(total);
//            pedido.setPendiente(pedido.getTotal().subtract(pedido.getAbonado()));

            pedidoRepository.save(pedido);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void saveEmpleadoEntrega(PedidoDTO pedidoDTO) throws ServiceLayerException {
        try {

            Optional<Pedido> pedidoOptional = pedidoRepository.findById(pedidoDTO.getIdPedido());
            if (pedidoOptional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(pedidoDTO.getIdPedido()));
            }

            Empleado empleado = new Empleado(pedidoDTO.getEmpleadoEntrega().getIdEmpleado());

            Pedido pedido = pedidoOptional.get();
            pedido.setEstatusPedidoPrevio(new EstatusPedido(pedido.getEstatusPedido().getIdEstatusPedido()));
            pedido.setEstatusPedido(new EstatusPedido(EstatusPedidoEnum.ASIGNADO.getValue()));
            pedido.setEmpleadoEntrega(empleado);

            pedidoRepository.save(pedido);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void removeEmpleadoEntrega(PedidoDTO pedidoDTO) throws ServiceLayerException {
        try {

            Optional<Pedido> pedidoOptional = pedidoRepository.findById(pedidoDTO.getIdPedido());
            if (pedidoOptional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(pedidoDTO.getIdPedido()));
            }
            Pedido pedido = pedidoOptional.get();

            EstatusPedido estatusPedido = pedido.getEstatusPedido();
            if (applicationUtil.nonNull(pedido.getEstatusPedidoPrevio())) {
                estatusPedido = pedido.getEstatusPedidoPrevio();
            }

            pedido.setEmpleadoEntrega(null);
            pedido.setEstatusPedido(estatusPedido);

            pedidoRepository.save(pedido);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void saveDelivery(PedidoDTO pedidoDTO, String token) throws ServiceLayerException {
        try {

            Optional<Pedido> pedidoOptional = pedidoRepository.findById(pedidoDTO.getIdPedido());
            if (pedidoOptional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(pedidoDTO.getIdPedido()));
            }
            Pedido pedido = pedidoOptional.get();

            Date now = new Date();

            EstatusPedido estatusPrevio = new EstatusPedido(pedido.getEstatusPedido().getIdEstatusPedido());
            EstatusPedido estatusEntregado = new EstatusPedido(EstatusPedidoEnum.ENTREGADO.getValue());

            Empleado empleadoEntrega = jwtService.getEmpleado(token);

            pedido.setFechaEntrega(now);
            pedido.setEstatusPedido(estatusEntregado);
            pedido.setEstatusPedidoPrevio(estatusPrevio);
            pedido.setEmpleadoEntrega(empleadoEntrega);


            pedidoRepository.save(pedido);
            pedidoProductoService.saveAllDeliveryProducts(pedidoDTO.getProductos());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void save(PedidoDTO pedidoDTO, String token) throws ServiceLayerException {
        try {


            Cliente cliente = clienteRepository.findByIdCliente(pedidoDTO.getCliente().getIdCliente());
            EstatusPedido estatusPedido = new EstatusPedido(EstatusPedidoEnum.PENDIENTE.getValue());
            String prefix = applicationUtil.getPrefixPedido(TipoClienteEnum.REGULAR.getValue());
            Domicilio domicilio = domicilioRepository.findByCliente(cliente);
            Empleado empleado = jwtService.getEmpleado(token);
            Date now = new Date();

            Pedido pedido = new Pedido();
            pedido.setClave(System.currentTimeMillis() + "");
            pedido.setFechaRegistro(now);
            pedido.setFechaSolicitud(pedidoDTO.getFechaSolicitud());
            pedido.setCliente(cliente);
            pedido.setEstatusPedidoPrevio(estatusPedido);
            pedido.setEstatusPedido(estatusPedido);
            pedido.setTotal(BigDecimal.ZERO);
            pedido.setAbonado(BigDecimal.ZERO);
            pedido.setPendiente(BigDecimal.ZERO);
            pedido.setDomicilio(domicilio);
            pedido.setRuta(cliente.getRuta());
            pedidoRepository.save(pedido);

            pedidoDTO.setIdPedido(pedido.getIdPedido());
            BigDecimal total = pedidoProductoService.saveProductosPedido(pedidoDTO, empleado.getIdEmpleado(), cliente.getIdCliente());

            pedido.setTotal(total);
            pedido.setPendiente(total);
            pedido.setClave(prefix + pedido.getIdPedido());
            pedidoRepository.save(pedido);

            cuentaService.updateSaldo(cliente.getIdCliente());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public List<PedidoDTO> findByCliente(Long idCliente) throws ServiceLayerException {
        try {

            List<Pedido> pedidos = pedidoRepository.findAllByCliente(idCliente);
            if (applicationUtil.isEmptyList(pedidos)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }
            return pedidos.stream().map(pedido -> PedidoDTO.builder()
                    .idPedido(pedido.getIdPedido())
                    .clave(pedido.getClave())
                    .fechaEntrega(pedido.getFechaEntrega())
                    .fechaSolicitud(pedido.getFechaSolicitud())
                    .total(pedido.getTotal())
                    .build()).toList();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }


}