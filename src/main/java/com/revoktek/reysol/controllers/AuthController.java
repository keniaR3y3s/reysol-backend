package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.AuthPath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.UsuarioDTO;
import com.revoktek.reysol.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(AuthPath.CONTROLLER)
public class AuthController {

    private AuthService authService;


    @PutMapping(AuthPath.SAVE)
    public ResponseEntity<?> save(@RequestBody UsuarioDTO empleadoDTO) throws ServiceLayerException {
       return ResponseEntity.ok(authService.saveUser(empleadoDTO));
    }


    @PostMapping(AuthPath.LOGIN)
    public ResponseEntity<?> login(@RequestBody UsuarioDTO authDTO) throws ServiceLayerException {
        return ResponseEntity.ok(authService.login(authDTO));
    }


    @PostMapping(AuthPath.REFRESH)
    public ResponseEntity<?> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws ServiceLayerException {
        return authService.refreshToken(token);
    }


}
