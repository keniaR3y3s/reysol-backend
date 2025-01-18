package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {
}