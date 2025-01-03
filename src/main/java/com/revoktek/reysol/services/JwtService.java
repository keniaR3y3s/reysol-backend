package com.revoktek.reysol.services;

import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Usuario;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {


    String generateToken(Usuario usuarioDTO);

    String refreshToken(Usuario usuarioDTO);

    Long getExpiration();

    Long getIdUser(String token);

    String getUsuario(String jwtToken);

    boolean isTokenValid(String jwt, UserDetails userDetails);

    Empleado getEmpleado(String token);
}
