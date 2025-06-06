package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogAuditoria, Long> {
}