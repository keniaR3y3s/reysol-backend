package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
            SELECT
                pedido
            FROM Pedido pedido
            INNER JOIN FETCH pedido.cliente cliente
            LEFT JOIN FETCH cliente.domicilio domicilio
            LEFT JOIN FETCH cliente.contacto contacto
            INNER JOIN FETCH cliente.tipoCliente tipoCliente
            INNER JOIN FETCH pedido.estatusPedido estatus
            LEFT JOIN FETCH pedido.estatusPago estatusPago
            LEFT JOIN pedido.ruta ruta
            INNER JOIN pedido.estatusPedido estatusPedido
            LEFT JOIN pedido.empleadoEntrega empleadoEntrega
            WHERE
                ( (:fechaInicio IS NULL OR :fechaFin IS NULL) OR (pedido.fechaSolicitud BETWEEN :fechaInicio AND :fechaFin) )
                AND (:idRuta IS NULL OR ruta.idRuta = :idRuta)
                AND (:estatusList IS NULL OR estatusPedido.idEstatusPedido IN (:estatusList))
                AND (:estatusPagoList IS NULL OR estatusPago.idEstatusPago IN (:estatusPagoList))
                AND (:idEmpleadoEntrega IS NULL OR empleadoEntrega.idEmpleado = :idEmpleadoEntrega)
                AND (:idTipoCliente IS NULL OR tipoCliente.idTipoCliente = :idTipoCliente)
                AND ((:busqueda IS NULL OR cliente.alias ILIKE CONCAT('%', :busqueda, '%')))
            ORDER BY pedido.fechaSolicitud DESC
            """)
    List<Pedido> findAllByFilter(
            @Param("fechaInicio") Date fechaInicio,
            @Param("fechaFin") Date fechaFin,
            @Param("idRuta") Integer idRuta,
            @Param("estatusList") List<Integer> estatusList,
            @Param("estatusPagoList") List<Integer> estatusPagoList,
            @Param("idEmpleadoEntrega") Long idEmpleadoEntrega,
            @Param("busqueda") String busqueda,
            @Param("idTipoCliente") Long idTipoCliente);

    @Query("SELECT MAX(p.idPedido) FROM Pedido p")
    Long getMaxId();

    Pedido findByIdPedido(Long idPedido);

    @Query("""
            SELECT
                pedido
            FROM Pedido pedido
            INNER JOIN FETCH pedido.cliente cliente
            INNER JOIN FETCH pedido.estatusPedido estatus
            WHERE
                ( cliente.idCliente = :idCliente )
            ORDER BY pedido.fechaSolicitud DESC
            """)
    List<Pedido> findAllByCliente(Long idCliente);

    @Query("""
            SELECT
                pedido
            FROM Pedido pedido
            INNER JOIN FETCH pedido.empleadoEntrega empleadoEntrega
            INNER JOIN FETCH pedido.cliente cliente
            INNER JOIN FETCH pedido.ruta ruta
            WHERE
                ( empleadoEntrega.idEmpleado = :idEmpleado )
            ORDER BY pedido.fechaSolicitud DESC
            """)
    List<Pedido> findAllByEmpleadoEntrega(Long idEmpleado);
}
