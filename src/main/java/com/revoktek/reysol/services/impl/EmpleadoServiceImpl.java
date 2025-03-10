package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.ContactoDTO;
import com.revoktek.reysol.dto.EmpleadoDTO;
import com.revoktek.reysol.dto.RolDTO;
import com.revoktek.reysol.dto.UsuarioDTO;
import com.revoktek.reysol.persistence.entities.Contacto;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Rol;
import com.revoktek.reysol.persistence.entities.Usuario;
import com.revoktek.reysol.persistence.entities.UsuarioRol;
import com.revoktek.reysol.persistence.entities.UsuarioRolId;
import com.revoktek.reysol.persistence.repositories.UsuarioRolRepository;
import com.revoktek.reysol.persistence.repositories.ContactoRepository;
import com.revoktek.reysol.persistence.repositories.EmpleadoRepository;
import com.revoktek.reysol.persistence.repositories.UsuarioRepository;
import com.revoktek.reysol.services.EmpleadoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final UsuarioRolRepository usuarioRolRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationUtil applicationUtil;
    private final MessageProvider messageProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ContactoRepository contactoRepository;


    @Override
    public EmpleadoDTO findByID(Long id) throws ServiceLayerException {
        try {

            Optional<Empleado> optional = empleadoRepository.findById(id);
            if (optional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(id));
            }
            Empleado empleado = optional.get();
            Usuario usuario = empleado.getUsuario();

            EmpleadoDTO empleadoDTO = EmpleadoDTO.builder()
                    .idEmpleado(empleado.getIdEmpleado())
                    .nombre(empleado.getNombre())
                    .primerApellido(empleado.getPrimerApellido())
                    .segundoApellido(empleado.getSegundoApellido())
                    .fechaRegistro(empleado.getFechaRegistro())
                    .build();

            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setUsuario(usuario.getUsuario());
            usuarioDTO.setEstatus(usuario.getEstatus());
            usuarioDTO.setRoles(new ArrayList<>());

            List<UsuarioRol> usuarioRolList = usuarioRolRepository.findAllByUsuario(usuario);
            for (UsuarioRol usuarioRol : usuarioRolList) {
                RolDTO rolDTO = new RolDTO();
                rolDTO.setIdRol(usuarioRol.getRol().getIdRol());
                rolDTO.setNombre(usuarioRol.getRol().getNombre());
                rolDTO.setDescripcion(usuarioRol.getRol().getDescripcion());
                usuarioDTO.getRoles().add(rolDTO);
            }

            empleadoDTO.setUsuario(usuarioDTO);

            empleadoDTO.setContacto(new ContactoDTO());
            Contacto contacto = contactoRepository.findByEmpleado(empleado);
            if (applicationUtil.nonNull(contacto)) {
                empleadoDTO.getContacto().setIdContacto(contacto.getIdContacto());
                empleadoDTO.getContacto().setTelefono(contacto.getTelefono());
            }


            return empleadoDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public List<EmpleadoDTO> findAllByRol(Long idRol) throws ServiceLayerException {
        try {
            log.info("findAllByRol.idRol : {}", idRol);

            List<UsuarioRol> usuarioRols = usuarioRolRepository.findByRol(idRol);

            if (applicationUtil.isEmptyList(usuarioRols)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }
            log.info("{} elementos encontrados.", usuarioRols.size());

            return usuarioRols.stream().map(usuarioRol -> EmpleadoDTO.builder()
                    .idEmpleado(usuarioRol.getUsuario().getEmpleado().getIdEmpleado())
                    .nombre(usuarioRol.getUsuario().getEmpleado().getNombre())
                    .primerApellido(usuarioRol.getUsuario().getEmpleado().getPrimerApellido())
                    .segundoApellido(usuarioRol.getUsuario().getEmpleado().getSegundoApellido())
                    .build()).toList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public List<EmpleadoDTO> findAllByFilter(String busqueda) throws ServiceLayerException {
        try {

            List<Empleado> empleados = empleadoRepository.findAllByFilter(busqueda);

            if (applicationUtil.isEmptyList(empleados)) {
                log.info("Sin elementos encontrados.");
                return Collections.emptyList();
            }

            log.info("{} elementos encontrados.", empleados.size());

            List<EmpleadoDTO> dtoList = empleados.stream().map(empleado -> {
                Usuario usuario = empleado.getUsuario();

                UsuarioDTO usuarioDTO = new UsuarioDTO();
                usuarioDTO.setIdUsuario(usuario.getIdUsuario());
                usuarioDTO.setUsuario(usuario.getUsuario());
                usuarioDTO.setEstatus(usuario.getEstatus());

                return EmpleadoDTO.builder()
                        .idEmpleado(empleado.getIdEmpleado())
                        .nombre(empleado.getNombre())
                        .primerApellido(empleado.getPrimerApellido())
                        .segundoApellido(empleado.getSegundoApellido())
                        .usuario(usuarioDTO)
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
    public void save(EmpleadoDTO empleadoDTO) throws ServiceLayerException {
        try {

            UsuarioDTO usuarioDTO = empleadoDTO.getUsuario();

            Optional<Usuario> optionalUsuario = usuarioRepository.findByUsuario(usuarioDTO.getUsuario());
            if (optionalUsuario.isPresent()) {
                throw new ServiceLayerException("Usuario previamente registrado");
            }

            if (applicationUtil.isEmptyList(usuarioDTO.getRoles())) {
                throw new ServiceLayerException("Ingrese roles para el usuario");
            }

            Usuario usuario = Usuario.builder()
                    .usuario(usuarioDTO.getUsuario())
                    .contrasena(passwordEncoder.encode(usuarioDTO.getContrasena()))
                    .estatus(Boolean.TRUE)
                    .build();
            usuarioRepository.save(usuario);


            Empleado empleado = new Empleado();
            empleado.setNombre(empleadoDTO.getNombre());
            empleado.setPrimerApellido(empleadoDTO.getPrimerApellido());
            empleado.setSegundoApellido(empleadoDTO.getSegundoApellido());
            empleado.setFechaRegistro(new Date());
            empleado.setUsuario(usuario);
            empleadoRepository.save(empleado);


            List<UsuarioRol> usuarioRoles = new ArrayList<>();
            for (RolDTO rolDTO : usuarioDTO.getRoles()) {
                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setUsuario(usuario);
                usuarioRol.setRol(new Rol(rolDTO.getIdRol()));
                UsuarioRolId usuarioRolId = new UsuarioRolId();
                usuarioRolId.setRolId(usuarioRolId.getRolId());
                usuarioRolId.setUsuarioId(usuarioRolId.getUsuarioId());
                usuarioRol.setId(usuarioRolId);
                usuarioRoles.add(usuarioRol);
            }
            usuarioRolRepository.saveAll(usuarioRoles);


            Contacto contacto = new Contacto();
            contacto.setEmpleado(empleado);
            contacto.setTelefono(empleadoDTO.getContacto().getTelefono());
            contactoRepository.save(contacto);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void update(EmpleadoDTO empleadoDTO) throws ServiceLayerException {
        try {

            Empleado empleado = empleadoRepository.findByIdEmpleado(empleadoDTO.getIdEmpleado());
            Usuario usuario = empleado.getUsuario();

            UsuarioDTO usuarioDTO = empleadoDTO.getUsuario();

            Optional<Usuario> optionalUsuario = usuarioRepository.findByUsuarioAndIdUsuarioNot(usuarioDTO.getUsuario(), usuario.getIdUsuario());
            if (optionalUsuario.isPresent()) {
                throw new ServiceLayerException("Usuario previamente registrado");
            }

            if (applicationUtil.isEmptyList(usuarioDTO.getRoles())) {
                throw new ServiceLayerException("Ingrese roles para el usuario");
            }

            empleado.setNombre(empleadoDTO.getNombre());
            empleado.setPrimerApellido(empleadoDTO.getPrimerApellido());
            empleado.setSegundoApellido(empleadoDTO.getSegundoApellido());
            empleadoRepository.save(empleado);

            usuario.setUsuario(usuarioDTO.getUsuario());
            if (applicationUtil.nonEmpty(usuarioDTO.getContrasena())) {
                usuario.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
            }
            usuario.setEstatus(usuarioDTO.getEstatus());
            usuarioRepository.save(usuario);

            List<UsuarioRol> usuarioRoles = usuarioRolRepository.findAllByUsuario(usuario);
            if (applicationUtil.nonEmptyList(usuarioRoles)) {
                usuarioRolRepository.deleteAll(usuarioRoles);
            }

            usuarioRoles = new ArrayList<>();
            for (RolDTO rolDTO : usuarioDTO.getRoles()) {
                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setUsuario(usuario);
                usuarioRol.setRol(new Rol(rolDTO.getIdRol()));
                usuarioRoles.add(usuarioRol);
            }
            usuarioRolRepository.saveAll(usuarioRoles);

            Contacto contacto = contactoRepository.findByEmpleado(empleado);
            if (applicationUtil.isNull(contacto)) {
                contacto = new Contacto();
            }
            contacto.setEmpleado(empleado);
            contacto.setTelefono(empleadoDTO.getContacto().getTelefono());
            contactoRepository.save(contacto);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }


}