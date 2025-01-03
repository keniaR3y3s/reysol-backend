package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.PrecioCliente;
import com.revoktek.reysol.persistence.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrecioClienteRepository extends JpaRepository<PrecioCliente, Long> {
    PrecioCliente findByProductoAndClienteAndEstatus(Producto producto, Cliente cliente, Boolean estatus);
}