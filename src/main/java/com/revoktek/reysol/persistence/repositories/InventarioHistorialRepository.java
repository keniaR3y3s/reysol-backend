package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.InventarioHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface InventarioHistorialRepository extends JpaRepository<InventarioHistorial, Long> {
    @Query("""
            SELECT
                inventarioHistorial
            FROM InventarioHistorial inventarioHistorial
            INNER JOIN FETCH inventarioHistorial.tipoMovimiento tipoMovimiento
            INNER JOIN FETCH inventarioHistorial.inventario inventario
            LEFT JOIN FETCH inventario.tipoInventario tipoInventario
            INNER JOIN FETCH inventario.producto producto
            LEFT JOIN FETCH producto.unidadMedida unidadMedida
            WHERE
                ( (:fechaInicio IS NULL OR :fechaFin IS NULL) OR (inventarioHistorial.fechaRegistro BETWEEN :fechaInicio AND :fechaFin) )
                 OR
                (:busqueda IS NULL OR producto.nombre ILIKE CONCAT('%', :busqueda, '%'))
            ORDER BY inventarioHistorial.fechaRegistro DESC
            """)
    List<InventarioHistorial> findAllByFilter(
            @Param("fechaInicio") Date fechaInicio,
            @Param("fechaFin") Date fechaFin,
            @Param("busqueda") String busqueda);
}