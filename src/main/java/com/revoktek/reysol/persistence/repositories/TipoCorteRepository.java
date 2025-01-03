package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.TipoCorte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCorteRepository extends JpaRepository<TipoCorte, Integer> {
    TipoCorte findByIdTipoCorte(Integer idTipoCorte);
}