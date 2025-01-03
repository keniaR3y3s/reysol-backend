package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

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

}