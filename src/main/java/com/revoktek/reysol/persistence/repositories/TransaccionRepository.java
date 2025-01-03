package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
}