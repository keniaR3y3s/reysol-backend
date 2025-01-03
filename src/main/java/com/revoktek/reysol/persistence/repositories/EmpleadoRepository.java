package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Empleado;
import com.revoktek.reysol.persistence.entities.Pedido;
import com.revoktek.reysol.persistence.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Empleado findByUsuario(Usuario usuario);

    @Query("""
                SELECT e
                FROM Empleado e
                INNER JOIN FETCH e.usuario u
                WHERE
                   (:busqueda IS NULL OR
                   CONCAT(e.nombre, ' ', e.primerApellido, ' ', COALESCE(e.segundoApellido, ''))
                   ILIKE CONCAT('%', :busqueda, '%'))
                ORDER BY e.nombre ASC
            """)
    List<Empleado> findAllByFilter(@Param("busqueda") String busqueda);


    Empleado findByIdEmpleado(Long idEmpleado);
}