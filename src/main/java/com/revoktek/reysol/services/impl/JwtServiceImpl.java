package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Usuario;
import com.revoktek.reysol.persistence.repositories.EmpleadoRepository;
import com.revoktek.reysol.persistence.repositories.UsuarioRepository;
import com.revoktek.reysol.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final ApplicationUtil applicationUtil;
    private final UsuarioRepository usuarioRepository;
    private final String BEARER = "Bearer ";
    private final MessageProvider messageProvider;
    private final EmpleadoRepository empleadoRepository;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private Long expiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private Long refreshExpiration;


    public JwtServiceImpl(ApplicationUtil applicationUtil, UsuarioRepository usuarioRepository, MessageProvider messageProvider, EmpleadoRepository empleadoRepository) {
        this.applicationUtil = applicationUtil;
        this.usuarioRepository = usuarioRepository;
        this.messageProvider = messageProvider;
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public String generateToken(Usuario usuario) {
        return buildToken(usuario, expiration);
    }

    @Override
    public String refreshToken(Usuario usuarioDTO) {
        return buildToken(usuarioDTO, refreshExpiration);
    }

    @Override
    public Long getExpiration() {
        return expiration;
    }

    @Override
    public Long getIdUser(String token) {
        try {
            String username = getUsuario(token);
            Optional<Usuario> optional = usuarioRepository.findByUsuario(username);
            if (optional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(username));
            }
            return optional.get().getIdUsuario();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String getUsuario(String token) {
        try {
            if (applicationUtil.isEmpty(token)) {
                throw new Exception("Token de solicitud no valido");
            }
            token = token.startsWith(BEARER) ? token.substring(BEARER.length()) : token;

            Claims jwtToken = Jwts.parser().verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return jwtToken.getSubject();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }


    @Override
    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        return true;
    }

    @Override
    public Empleado getEmpleado(String token) {
        try {
            Long idUsuario = getIdUser(token);
            Optional<Usuario> optional = usuarioRepository.findById(idUsuario);
            if (optional.isEmpty()) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(idUsuario));
            }
            Empleado empleado = empleadoRepository.findByUsuario(optional.get());
            if (applicationUtil.isNull(empleado)) {
                throw new ServiceLayerException(messageProvider.getMessageNotFound(idUsuario));
            }
            return empleado;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    private String buildToken(Usuario usuario, Long expirationMilis) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + expirationMilis);
        return Jwts.builder().
                id(usuario.getIdUsuario().toString())
                .claims(Map.of("usuario", usuario.getUsuario()))
                .subject(usuario.getUsuario())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
}
