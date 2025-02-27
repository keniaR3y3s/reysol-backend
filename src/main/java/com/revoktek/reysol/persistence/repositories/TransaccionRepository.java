package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Cuenta;
import com.revoktek.reysol.persistence.entities.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    @Query("""
                SELECT e
                FROM Transaccion e
                INNER JOIN FETCH e.tipoTransaccion tipoTransaccion
                LEFT JOIN FETCH e.pago pago
                LEFT JOIN FETCH pago.formaPago formaPago
                LEFT JOIN FETCH pago.metodoPago metodoPago
                LEFT JOIN FETCH pago.estatusPago estatusPago
                WHERE e.cuenta = :cuenta
                ORDER BY e.fechaRegistro DESC
            """)
    List<Transaccion> findAllByCuenta(@Param("cuenta") Cuenta cuenta);

}