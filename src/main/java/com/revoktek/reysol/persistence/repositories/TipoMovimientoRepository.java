package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoMovimientoRepository extends JpaRepository<TipoMovimiento, Long> {
}