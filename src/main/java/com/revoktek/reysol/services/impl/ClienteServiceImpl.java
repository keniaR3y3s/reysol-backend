package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.TipoClienteEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.core.utils.MapperUtil;
import com.revoktek.reysol.dto.ClienteDTO;
import com.revoktek.reysol.dto.ContactoDTO;
import com.revoktek.reysol.dto.DomicilioDTO;
import com.revoktek.reysol.dto.EmpleadoDTO;
import com.revoktek.reysol.dto.RutaDTO;
import com.revoktek.reysol.dto.TipoClienteDTO;
import com.revoktek.reysol.dto.UsuarioDTO;
import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Contacto;
import com.revoktek.reysol.persistence.entities.Domicilio;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Ruta;
import com.revoktek.reysol.persistence.entities.TipoCliente;
import com.revoktek.reysol.persistence.entities.Usuario;
import com.revoktek.reysol.persistence.repositories.ClienteRepository;
import com.revoktek.reysol.persistence.repositories.ContactoRepository;
import com.revoktek.reysol.persistence.repositories.DomicilioRepository;
import com.revoktek.reysol.services.ClienteService;
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
public class ClienteServiceImpl implements ClienteService {

    private final DomicilioRepository domicilioRepository;
    private final ContactoRepository contactoRepository;
    private final ClienteRepository clienteRepository;
    private final ApplicationUtil applicationUtil;
    private final MapperUtil mapperUtil;


    @Override
    public List<ClienteDTO> findAllByFilter(String busqueda) throws ServiceLayerException {
        try {

            Integer idTipoCliente = TipoClienteEnum.REGULAR.getValue();
            List<Cliente> clientes = clienteRepository.findAllByFilter(busqueda, idTipoCliente);

            if (applicationUtil.isEmptyList(clientes)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }

            log.info("{} elementos encontrados.", clientes.size());

            return clientes.stream().map(cliente -> ClienteDTO.builder()
                    .idCliente(cliente.getIdCliente())
                    .alias(cliente.getAlias())
                    .nombre(cliente.getNombre())
                    .primerApellido(cliente.getPrimerApellido())
                    .segundoApellido(cliente.getSegundoApellido())
                    .fechaRegistro(cliente.getFechaRegistro())
                    .build()).toList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public ClienteDTO findById(Long idCliente) throws ServiceLayerException {
        try {
            Cliente cliente = clienteRepository.findByIdCliente(idCliente);
            if (applicationUtil.nonNull(cliente)) {
                throw new ServiceLayerException("Cliente no encontrado.");
            }

            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setIdCliente(cliente.getIdCliente());
            clienteDTO.setAlias(cliente.getAlias());
            clienteDTO.setNombre(cliente.getNombre());
            clienteDTO.setPrimerApellido(cliente.getPrimerApellido());
            clienteDTO.setSegundoApellido(cliente.getSegundoApellido());
            clienteDTO.setFechaRegistro(cliente.getFechaRegistro());
            clienteDTO.setEstatus(cliente.getEstatus());

            clienteDTO.setTipoCliente(new TipoClienteDTO());
            clienteDTO.setDomicilio(new DomicilioDTO());
            clienteDTO.setContacto(new ContactoDTO());
            clienteDTO.setRuta(new RutaDTO());


            Ruta ruta = cliente.getRuta();
            if (applicationUtil.nonNull(ruta)) {
                clienteDTO.getRuta().setIdRuta(ruta.getIdRuta());
                clienteDTO.getRuta().setNombre(ruta.getNombre());
            }

            TipoCliente tipoCliente = cliente.getTipoCliente();
            if (applicationUtil.nonNull(tipoCliente)) {
                clienteDTO.getTipoCliente().setIdTipoCliente(tipoCliente.getIdTipoCliente());
                clienteDTO.getTipoCliente().setNombre(tipoCliente.getNombre());
            }

            Contacto contacto = contactoRepository.findByCliente(cliente);
            if (applicationUtil.nonNull(contacto)) {
                clienteDTO.getContacto().setIdContacto(contacto.getIdContacto());
                clienteDTO.getContacto().setTelefono(contacto.getTelefono());
            }

            Domicilio domicilio = domicilioRepository.findByCliente(cliente);
            if (applicationUtil.nonNull(domicilio)) {
                clienteDTO.getDomicilio().setIdDomicilio(domicilio.getIdDomicilio());
                clienteDTO.getDomicilio().setCalle(domicilio.getCalle());
                clienteDTO.getDomicilio().setNumInt(domicilio.getNumInt());
                clienteDTO.getDomicilio().setNumExt(domicilio.getNumExt());
                clienteDTO.getDomicilio().setColonia(domicilio.getColonia());
                clienteDTO.getDomicilio().setMunicipio(domicilio.getMunicipio());
                clienteDTO.getDomicilio().setEstado(domicilio.getEstado());
            }

            return clienteDTO;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void changeEstatus(Long idCliente) throws ServiceLayerException {

    }

    @Override
    @Transactional
    public void save(ClienteDTO clienteDTO) throws ServiceLayerException {
        try {
            Cliente cliente = clienteRepository.findByAlias(clienteDTO.getAlias());
            if (applicationUtil.nonNull(cliente)) {
                throw new ServiceLayerException("Alias registrado previamente");
            }

            TipoCliente tipoCliente = new TipoCliente(TipoClienteEnum.REGULAR.getValue());

            cliente = mapperUtil.parseBetweenObject(Cliente.class, clienteDTO);
            cliente.setEstatus(Boolean.TRUE);
            cliente.setFechaRegistro(new Date());
            cliente.setTipoCliente(tipoCliente);
            if (applicationUtil.nonNull(clienteDTO.getRuta())
                    && applicationUtil.nonNull(clienteDTO.getRuta().getIdRuta())) {
                cliente.setRuta(new Ruta(clienteDTO.getRuta().getIdRuta()));
            }
            clienteRepository.save(cliente);

            if (applicationUtil.nonNull(clienteDTO.getContacto())
                    && applicationUtil.nonEmpty(clienteDTO.getContacto().getTelefono())) {
                Contacto contacto = new Contacto();
                contacto.setTelefono(clienteDTO.getContacto().getTelefono());
                contacto.setCliente(cliente);
                contactoRepository.save(contacto);
            }
            if (applicationUtil.nonNull(clienteDTO.getDomicilio())
                    && applicationUtil.nonEmpty(clienteDTO.getDomicilio().getCalle())) {
                Domicilio domicilio = mapperUtil.parseBetweenObject(Domicilio.class, clienteDTO.getDomicilio());
                domicilio.setCliente(cliente);
                domicilioRepository.save(domicilio);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }

    }

    @Override
    @Transactional
    public void update(ClienteDTO clienteDTO) throws ServiceLayerException {
        try {
            Cliente cliente = clienteRepository.findByAliasAndIdClienteNot(clienteDTO.getAlias(), clienteDTO.getIdCliente());
            if (applicationUtil.nonNull(cliente)) {
                throw new ServiceLayerException("Alias registrado previamente");
            }

            cliente = clienteRepository.findByIdCliente(clienteDTO.getIdCliente());
            cliente.setAlias(cliente.getAlias());
            cliente.setEstatus(clienteDTO.getEstatus());
            cliente.setNombre(clienteDTO.getNombre());
            cliente.setPrimerApellido(clienteDTO.getPrimerApellido());
            cliente.setSegundoApellido(clienteDTO.getSegundoApellido());

            if (applicationUtil.nonNull(clienteDTO.getRuta())
                    && applicationUtil.nonNull(clienteDTO.getRuta().getIdRuta())) {
                cliente.setRuta(new Ruta(clienteDTO.getRuta().getIdRuta()));
            } else {
                cliente.setRuta(null);
            }
            clienteRepository.save(cliente);

            Contacto contacto = contactoRepository.findByCliente(cliente);
            if (applicationUtil.nonNull(clienteDTO.getContacto())
                    && applicationUtil.nonEmpty(clienteDTO.getContacto().getTelefono())) {
                contacto = applicationUtil.isNull(contacto) ? new Contacto() : contacto;
                contacto.setTelefono(clienteDTO.getContacto().getTelefono());
                contactoRepository.save(contacto);
            } else if (applicationUtil.nonNull(contacto)) {
                contactoRepository.delete(contacto);
            }

            Domicilio domicilio = domicilioRepository.findByCliente(cliente);
            if (applicationUtil.isNull(domicilio)) {
                domicilio = mapperUtil.parseBetweenObject(Domicilio.class, clienteDTO.getDomicilio());
                domicilio.setCliente(cliente);
            } else {
                Long idDomicilio = domicilio.getIdDomicilio();
                domicilio = mapperUtil.parseBetweenObject(Domicilio.class, clienteDTO.getDomicilio());
                domicilio.setIdDomicilio(idDomicilio);
            }
            domicilioRepository.save(domicilio);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public ClienteDTO saveExtemporaneo() {
        try {

            TipoCliente tipoCliente = new TipoCliente();
            tipoCliente.setIdTipoCliente(TipoClienteEnum.EXTEMPORANEO.getValue());

            Cliente cliente = new Cliente();
            cliente.setAlias(System.currentTimeMillis() + "");
            cliente.setFechaRegistro(new Date());
            cliente.setEstatus(Boolean.TRUE);
            cliente.setTipoCliente(tipoCliente);

            clienteRepository.save(cliente);

            String alias = "EXTEMPORÁNEO-" + cliente.getIdCliente();
            cliente.setAlias(alias);

            clienteRepository.save(cliente);


            return ClienteDTO.builder()
                    .idCliente(cliente.getIdCliente())
                    .alias(cliente.getAlias())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }
}