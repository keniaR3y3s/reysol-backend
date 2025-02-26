package com.revoktek.reysol.persistence.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revoktek.reysol.persistence.entities.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query("""
            SELECT
                SUM(pago.monto)
            FROM Pago pago
            INNER JOIN  pago.estatusPago estatusPago
            INNER JOIN  pago.pedido pedido
            WHERE
                pedido.idPedido = :idPedido
                AND
                estatusPago.idEstatusPago = :idEstatusPago
            """)
    BigDecimal findAbonadoByPedido(@Param("idPedido") Long idPedido, @Param("idEstatusPago") Integer idEstatusPago);

    @Query("""
            SELECT p
            FROM Pago p
            INNER JOIN FETCH p.pedido pedido
            WHERE pedido.idPedido = :idPedido
            ORDER BY p.fechaRegistro DESC
            """)
            List<Pago> findByPedidoId(Long idPedido);

}