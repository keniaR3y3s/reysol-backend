package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Corte;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.TipoCorte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CorteRepository extends JpaRepository<Corte, Long> {
    Corte findByProductoAndTipoCorte(Producto producto, TipoCorte tipoCorte);

    @Query("""
            SELECT
                corte
            FROM Corte corte
            INNER JOIN FETCH corte.producto producto
            INNER JOIN FETCH producto.unidadMedida unidadMedida
            WHERE
               corte.tipoCorte = :tipoCorte AND corte.estatus = true
            ORDER BY producto.nombre ASC
            """)
    List<Corte> findAllByTipoCorte(@Param("tipoCorte") TipoCorte tipoCorte);
}