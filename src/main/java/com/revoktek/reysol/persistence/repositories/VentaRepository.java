package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}