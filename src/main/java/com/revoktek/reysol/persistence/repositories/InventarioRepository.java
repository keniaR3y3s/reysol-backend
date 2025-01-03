package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Inventario;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.TipoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Inventario findByProductoAndTipoInventario(Producto producto, TipoInventario tipoInventario);

    @Query("""
            SELECT
                inventario
            FROM Inventario inventario
            LEFT JOIN FETCH inventario.tipoInventario tipoInventario
            INNER JOIN FETCH inventario.producto producto
            LEFT JOIN FETCH producto.unidadMedida unidadMedida
            WHERE
                ( (:fechaInicio IS NULL OR :fechaFin IS NULL) OR (inventario.fechaModificacion BETWEEN :fechaInicio AND :fechaFin) )
                OR
                (:busqueda IS NULL OR producto.nombre ILIKE CONCAT('%', :busqueda, '%'))
            ORDER BY inventario.fechaModificacion DESC
            """)
    List<Inventario> findAllByFilter(
            @Param("fechaInicio") Date fechaInicio,
            @Param("fechaFin") Date fechaFin,
            @Param("busqueda") String busqueda);

    Inventario findByIdInventario(Long idInventario);

    @Query("""
            SELECT
                SUM(inventario.cantidad)
            FROM Inventario inventario
            WHERE inventario.producto = :producto
            """)
    BigDecimal findCantidadByProducto(@Param("producto") Producto producto);
}