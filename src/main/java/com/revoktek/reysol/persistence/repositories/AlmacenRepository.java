package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
}