package com.revoktek.reysol.config;

import com.revoktek.reysol.persistence.entities.LogAuditoria;
import com.revoktek.reysol.services.LogAuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class LogAuditoriaInterceptor implements HandlerInterceptor {

    private final LogAuditoriaService logService;

    private static final String INICIO_TIEMPO = "inicioTiempo";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(INICIO_TIEMPO, System.currentTimeMillis());
        return true; // continúa la ejecución
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
//        LogAuditoria log = new LogAuditoria();
//        log.setMetodoHttp(request.getMethod());
//        log.setUri(request.getRequestURI());
//        log.setHttpStatus(response.getStatus());
//        String usuario = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "ANONIMO";
//        log.setUsuario(usuario);
//        log.setResultado(ex == null ? "OK" : "ERROR: " + ex.getMessage());
//        logService.save(log);
    }

}
