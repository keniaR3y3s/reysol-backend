package com.revoktek.reysol.persistence.repositories;

import java.math.BigDecimal;
import java.util.List;

import com.revoktek.reysol.persistence.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    @Query("""
                    SELECT c
                    FROM Cuenta c
                    INNER JOIN FETCH c.cliente e
                    WHERE e.idCliente = :idCliente
            """)
    Cuenta findByCliente(Long idCliente);

    Cuenta findByIdCuenta(Long idCuenta);


    @Query("""
                    SELECT c 
                    FROM Cuenta c 
                    INNER JOIN FETCH c.cliente e
                    WHERE (:busqueda IS NULL OR 
                        LOWER(CONCAT(e.nombre, ' ', e.primerApellido, ' ', COALESCE(e.segundoApellido, ''))) 
                        LIKE LOWER(CONCAT('%', :busqueda, '%'))
                        OR LOWER(e.alias) LIKE LOWER(CONCAT('%', :busqueda, '%')))
                    ORDER BY e.nombre ASC
            """)
    List<Cuenta> findAllByFilter(@Param("busqueda") String busqueda);

    @Query("""
                SELECT COALESCE(SUM(COALESCE(pedido.pendiente, 0)), 0)
                FROM Pedido pedido
                INNER JOIN pedido.cliente cliente
                INNER JOIN pedido.estatusPago estatusPago
                WHERE cliente.idCliente = :idCliente
                AND estatusPago.idEstatusPago NOT IN (:estatusList)
            """)
    BigDecimal sumPending(@Param("idCliente") Long idCliente, @Param("estatusList") List<Integer> estatusList);
}