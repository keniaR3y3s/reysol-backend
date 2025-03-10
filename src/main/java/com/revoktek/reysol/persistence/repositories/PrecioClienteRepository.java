package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.PrecioCliente;
import com.revoktek.reysol.persistence.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrecioClienteRepository extends JpaRepository<PrecioCliente, Long> {
    PrecioCliente findByProductoAndClienteAndEstatus(Producto producto, Cliente cliente, Boolean estatus);


    @Query("""
            SELECT
                precioCliente
            FROM PrecioCliente precioCliente
            INNER JOIN FETCH precioCliente.producto producto
            WHERE precioCliente.cliente = :cliente AND precioCliente.estatus = true
            ORDER BY producto.nombre ASC
            """)
    List<PrecioCliente> findAllByCliente(Cliente cliente);

    PrecioCliente findByProductoAndCliente(Producto producto, Cliente cliente);

}