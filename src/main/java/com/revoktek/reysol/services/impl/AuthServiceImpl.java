package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.RolDTO;
import com.revoktek.reysol.dto.UsuarioDTO;
import com.revoktek.reysol.persistence.entities.Rol;
import com.revoktek.reysol.persistence.entities.Usuario;
import com.revoktek.reysol.persistence.entities.UsuarioRol;
import com.revoktek.reysol.persistence.repositories.UsuarioRolRepository;
import com.revoktek.reysol.persistence.repositories.RolRepository;
import com.revoktek.reysol.persistence.repositories.UsuarioRepository;
import com.revoktek.reysol.services.AuthService;
import com.revoktek.reysol.dto.auth.TokenDTO;
import com.revoktek.reysol.services.JwtService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RolRepository rolRepository;
    private UsuarioRolRepository usuarioRolRepository;
    private final ApplicationUtil applicationUtil;
    private UsuarioRepository usuarioRepository;
    private MessageProvider messageProvider;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;


    @Override
    @Transactional
    public TokenDTO saveUser(UsuarioDTO usuarioDTO) throws ServiceLayerException {
        try {
            log.info("saverUser.usuarioDTO:{}", usuarioDTO);

            Optional<Usuario> optional = usuarioRepository.findByUsuario(usuarioDTO.getUsuario());
            if (optional.isPresent()) {
                throw new ServiceLayerException("Usuario previamente registrado");
            }

            if (applicationUtil.isEmptyList(usuarioDTO.getRoles())) {
                throw new ServiceLayerException("Ingrese un rol");
            }

            Usuario usuario = Usuario.builder()
                    .usuario(usuarioDTO.getUsuario())
                    .contrasena(passwordEncoder.encode(usuarioDTO.getContrasena()))
                    .estatus(Boolean.TRUE)
                    .build();

            usuarioRepository.save(usuario);

            List<UsuarioRol> usuarioRoles = new ArrayList<>();
            for (RolDTO rolDTO : usuarioDTO.getRoles()) {
                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setUsuario(usuario);
                usuarioRol.setRol(new Rol(rolDTO.getIdRol()));
                usuarioRoles.add(usuarioRol);
            }
            usuarioRolRepository.saveAll(usuarioRoles);

            List<RolDTO> roles = usuarioDTO.getRoles();
            for (RolDTO rolDTO : roles) {
                Optional<Rol> rolOptional = rolRepository.findById(rolDTO.getIdRol());
                if (rolOptional.isPresent()) {
                    Rol rol = rolOptional.get();
                    rolDTO.setNombre(rol.getNombre());
                    rolDTO.setDescripcion(rol.getDescripcion());
                }
            }

            String token = jwtService.generateToken(usuario);
            String refresh = jwtService.refreshToken(usuario);
            Long expiration = jwtService.getExpiration();
            Long idEmpleado = jwtService.getEmpleado(token).getIdEmpleado();

            return new TokenDTO(token, refresh, expiration, idEmpleado, roles);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public TokenDTO login(UsuarioDTO usuarioDTO) {
        try {
            log.info("login.usuarioDTO:{}", usuarioDTO);
            Optional<Usuario> optional = usuarioRepository.findByUsuarioAndEstatus(usuarioDTO.getUsuario(), Boolean.TRUE);
            if (optional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(usuarioDTO.getUsuario()));
            }
            Usuario usuario = optional.get();
            String token = jwtService.generateToken(usuario);
            String refresh = jwtService.refreshToken(usuario);
            Long expiration = jwtService.getExpiration();
            Long idEmpleado = jwtService.getEmpleado(token).getIdEmpleado();

            List<RolDTO> roles = new ArrayList<>();
            List<UsuarioRol> usuarioRolList = usuarioRolRepository.findAllByUsuario(usuario);
            for (UsuarioRol usuarioRol : usuarioRolList) {
                RolDTO rolDTO = new RolDTO();
                rolDTO.setIdRol(usuarioRol.getRol().getIdRol());
                rolDTO.setNombre(usuarioRol.getRol().getNombre());
                rolDTO.setDescripcion(usuarioRol.getRol().getDescripcion());
                roles.add(rolDTO);
            }
            TokenDTO tokenDTO = new TokenDTO(token, refresh, expiration, idEmpleado, roles);
            System.out.println(tokenDTO.toString());
            return tokenDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public ResponseEntity<?> refreshToken(String token) {
        return null;
    }
}
