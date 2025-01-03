package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Cuenta findByCliente(Cliente cliente);

    Cuenta findByIdCuenta(Long idCuenta);
}