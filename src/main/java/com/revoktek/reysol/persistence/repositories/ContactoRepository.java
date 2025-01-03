package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Contacto;
import com.revoktek.reysol.persistence.entities.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    Contacto findByEmpleado(Empleado empleado);

    Contacto findByCliente(Cliente cliente);
}