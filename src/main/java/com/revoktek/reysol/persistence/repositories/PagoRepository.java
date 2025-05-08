package com.revoktek.reysol.persistence.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revoktek.reysol.persistence.entities.Pago;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    @Query("""
                SELECT COALESCE(SUM(pago.monto), 0)
                FROM Pago pago
                JOIN pago.estatusPago estatusPago
                JOIN pago.pedido pedido
                WHERE pedido.idPedido = :idPedido
                AND estatusPago.idEstatusPago IN (:estatusList)
            """)
    BigDecimal findAbonadoByPedido(@Param("idPedido") Long idPedido, @Param("estatusList") List<Integer> estatusList);


    @Query("""
            SELECT p
            FROM Pago p
            INNER JOIN FETCH p.pedido pedido
             LEFT JOIN FETCH p.cancelacionPago cancelacionPago
            WHERE pedido.idPedido = :idPedido
            ORDER BY p.fechaRegistro DESC
            """)
    List<Pago> findByPedidoId(Long idPedido);

    @Query("""
            SELECT p
            FROM Pago p
            WHERE p.idPago = :idPago
            """)
    Optional<Pago> findByPagoId(@Param("idPago") Long idPago);

    @Query("""
            SELECT p
            FROM Pago p
            INNER JOIN FETCH p.cuenta cuenta
            LEFT JOIN FETCH p.pedido pedido
            LEFT JOIN FETCH p.metodoPago metodoPago
            LEFT JOIN FETCH p.formaPago formaPago
            LEFT JOIN FETCH p.estatusPago estatusPago
            LEFT JOIN FETCH p.empleado empleado
            LEFT JOIN FETCH p.cancelacionPago cancelacionPago
            LEFT JOIN FETCH cancelacionPago.empleado empleadoCancelacion
            WHERE cuenta.idCuenta = :idCuenta
            ORDER BY p.fechaRegistro DESC
            """)
    List<Pago> findAllByCuenta(Long idCuenta);
}