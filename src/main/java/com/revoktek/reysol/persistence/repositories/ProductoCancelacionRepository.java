package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.PedidoProducto;
import com.revoktek.reysol.persistence.entities.ProductoCancelacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoCancelacionRepository extends JpaRepository<ProductoCancelacion, Long> {

    ProductoCancelacion findByPedidoProducto(PedidoProducto pedidoProducto);

}