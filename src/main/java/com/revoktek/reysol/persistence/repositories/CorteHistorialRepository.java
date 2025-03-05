package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Corte;
import com.revoktek.reysol.persistence.entities.CorteHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorteHistorialRepository extends JpaRepository<CorteHistorial, Long> {

    List<CorteHistorial> findAllByCorteOrderByFechaRegistroDesc(Corte corte);

}