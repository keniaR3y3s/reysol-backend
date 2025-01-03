package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Pedido;
import com.revoktek.reysol.persistence.entities.PedidoProducto;
import com.revoktek.reysol.persistence.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PedidoProductoRepository extends JpaRepository<PedidoProducto, Long> {
    @Query("""
            SELECT
                pedidoProducto
            FROM PedidoProducto pedidoProducto
            INNER JOIN FETCH pedidoProducto.inventario inventario
            INNER JOIN FETCH inventario.producto producto
            INNER JOIN FETCH producto.unidadMedida unidadMedida
            WHERE pedidoProducto.pedido.idPedido = :idPedido
            ORDER BY producto.nombre ASC
            """)
    List<PedidoProducto> findAllByPedido(@Param("idPedido") Long idPedido);

    @Query("""
            SELECT
                SUM(pedidoProducto.cantidadSolicitada)
            FROM PedidoProducto pedidoProducto
            INNER JOIN  pedidoProducto.producto producto
            INNER JOIN  pedidoProducto.pedido pedido
            WHERE producto = :productoParam AND pedido.estatusPedido.idEstatusPedido = :idEstatusPedido
            """)
    BigDecimal sumCantidadsolicitadaByProductoAndEstatusPediddo(
            @Param("productoParam") Producto producto,
            @Param("idEstatusPedido") Integer idEstatusPedido
    );
}