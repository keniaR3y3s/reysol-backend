package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.dto.UsuarioDTO;
import com.revoktek.reysol.persistence.entities.Usuario;
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

import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private MessageProvider messageProvider;
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;


    @Override
    public TokenDTO saveUser(UsuarioDTO usuarioDTO) {
       try{
           log.info("saverUser.usuarioDTO:{}", usuarioDTO);

           Optional<Usuario> optional = usuarioRepository.findByUsuario(usuarioDTO.getUsuario());
           if(optional.isPresent()){
               throw new Exception(messageProvider.getMessageUnique(usuarioDTO.getUsuario()));
           }

           Usuario usuario = Usuario.builder()
                   .usuario(usuarioDTO.getUsuario())
                   .contrasena(passwordEncoder.encode(usuarioDTO.getContrasena()))
                   .estatus(Boolean.TRUE)
                   .build();

           usuarioRepository.save(usuario);

           String token = jwtService.generateToken(usuario);
           String refresh = jwtService.refreshToken(usuario);
           Long expiration = jwtService.getExpiration();
           Long idEmpleado = jwtService.getEmpleado(token).getIdEmpleado();

           return new TokenDTO(token, refresh, expiration, idEmpleado);
       }catch (Exception e){
           log.error(e.getMessage(), e);
           throw new ServiceLayerException(e);
       }
    }

    @Override
    public TokenDTO login(UsuarioDTO usuarioDTO) {
        log.info("login.usuarioDTO:{}", usuarioDTO);
        Optional<Usuario> optional = usuarioRepository.findByUsuario(usuarioDTO.getUsuario());
        if (optional.isEmpty()) {
            throw new ServiceLayerException(messageProvider.getMessageNotFound(usuarioDTO.getUsuario()));
        }
        Usuario usuario = optional.get();
        String token = jwtService.generateToken(usuario);
        String refresh = jwtService.refreshToken(usuario);
        Long expiration = jwtService.getExpiration();
        Long idEmpleado = jwtService.getEmpleado(token).getIdEmpleado();

        return new TokenDTO(token, refresh, expiration, idEmpleado);
    }

    @Override
    public ResponseEntity<?> refreshToken(String token) {
        return null;
    }
}
