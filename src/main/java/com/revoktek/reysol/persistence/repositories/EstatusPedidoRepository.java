package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.EstatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstatusPedidoRepository extends JpaRepository<EstatusPedido, Integer> {
}