package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.PrecioCliente;
import com.revoktek.reysol.persistence.entities.Producto;
import com.revoktek.reysol.persistence.entities.TipoCorte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrecioClienteRepository extends JpaRepository<PrecioCliente, Long> {
    PrecioCliente findByProductoAndClienteAndEstatusAndTipoCorte(Producto producto, Cliente cliente, Boolean estatus, TipoCorte tipoCorte);


    @Query("""
            SELECT
                precioCliente
            FROM PrecioCliente precioCliente
            INNER JOIN FETCH precioCliente.producto producto
            INNER JOIN FETCH precioCliente.tipoCorte tipoCorte
            WHERE precioCliente.cliente = :cliente AND precioCliente.estatus = true
            ORDER BY producto.nombre ASC
            """)
    List<PrecioCliente> findAllByCliente(Cliente cliente);


}