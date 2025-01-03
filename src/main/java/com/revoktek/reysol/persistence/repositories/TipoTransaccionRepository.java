package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.TipoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoTransaccionRepository extends JpaRepository<TipoTransaccion, Long> {
}