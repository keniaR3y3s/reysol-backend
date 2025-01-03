package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.TipoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoInventarioRepository extends JpaRepository<TipoInventario, Long> {
    TipoInventario findByIdTipoInventario(Integer idTipoInventatio);
}