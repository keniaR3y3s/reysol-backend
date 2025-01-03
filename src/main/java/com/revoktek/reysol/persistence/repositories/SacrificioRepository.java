package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Sacrificio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SacrificioRepository extends JpaRepository<Sacrificio, Long> {
}