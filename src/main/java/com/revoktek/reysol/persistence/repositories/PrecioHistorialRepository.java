package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.PrecioHistorial;
import com.revoktek.reysol.persistence.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PrecioHistorialRepository extends JpaRepository<PrecioHistorial, Long> {

    List<PrecioHistorial> findAllByProductoAndPrecioAndFechaRegistroLessThanEqualOrderByFechaRegistroDesc(Producto producto, BigDecimal precio, Date fechaRegistro);
}