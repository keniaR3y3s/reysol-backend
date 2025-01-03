package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.UsuarioDTO;
import com.revoktek.reysol.dto.auth.TokenDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    TokenDTO saveUser(UsuarioDTO empleadoDTO) throws ServiceLayerException;

    TokenDTO login(UsuarioDTO authDTO) throws ServiceLayerException;

    ResponseEntity<?> refreshToken(String token) throws ServiceLayerException;
}
