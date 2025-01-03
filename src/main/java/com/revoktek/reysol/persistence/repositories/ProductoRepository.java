package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Producto findByIdProducto(Long idProducto);

    @Query("""
            SELECT
                producto
            FROM Producto producto
            INNER JOIN FETCH producto.unidadMedida unidadMedida
            ORDER BY producto.nombre ASC
            """)
    List<Producto> findAllByFilter();

}