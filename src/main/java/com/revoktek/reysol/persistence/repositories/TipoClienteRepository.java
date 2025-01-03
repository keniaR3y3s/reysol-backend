package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoClienteRepository extends JpaRepository<TipoCliente, Long> {
}