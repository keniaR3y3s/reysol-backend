package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.persistence.entities.LogAuditoria;
import com.revoktek.reysol.persistence.repositories.LogRepository;
import com.revoktek.reysol.services.LogAuditoriaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class LogAuditoriaAuditoriaServiceImpl implements LogAuditoriaService {

    private LogRepository logRepository;



    @Override
    public void save(LogAuditoria logAuditoria) {
        try {
            logRepository.save(logAuditoria);
        } catch (Exception e) {
            log.error("Error al guardar en log {}", e.getMessage());
        }
    }
}