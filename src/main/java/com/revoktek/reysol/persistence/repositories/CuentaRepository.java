package com.revoktek.reysol.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revoktek.reysol.persistence.entities.Cliente;
import com.revoktek.reysol.persistence.entities.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Cuenta findByCliente(Cliente cliente);

    Cuenta findByIdCuenta(Long idCuenta);


    @Query("""
            SELECT c 
            FROM Cuenta c 
            INNER JOIN FETCH c.cliente e
            WHERE (:busqueda IS NULL OR 
                LOWER(CONCAT(e.nombre, ' ', e.primerApellido, ' ', COALESCE(e.segundoApellido, ''))) 
                LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR LOWER(e.alias) LIKE LOWER(CONCAT('%', :busqueda, '%')))
            ORDER BY e.nombre ASC
    """)
    List<Cuenta> findAllByFilter(@Param("busqueda") String busqueda);
}