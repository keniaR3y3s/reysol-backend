package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Integer> {
    Template findByNombre(String nombre);
}