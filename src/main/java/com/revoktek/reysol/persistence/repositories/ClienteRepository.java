package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByAlias(String alias);

    Cliente findByAliasAndIdClienteNot(String alias, Long idCliente);

    Cliente findByIdCliente(Long idCliente);


    @Query("""
                SELECT e
                FROM Cliente e
                INNER JOIN FETCH e.tipoCliente tipoCliente
                LEFT JOIN FETCH e.ruta ruta
                LEFT JOIN FETCH e.contacto contacto
                LEFT JOIN FETCH e.domicilio domicilio
                WHERE ( (:busqueda IS NULL) OR (CONCAT(e.nombre, ' ', e.primerApellido, ' ', COALESCE(e.segundoApellido, ''))
                            ILIKE CONCAT('%', :busqueda, '%')
                 ) OR ( e.alias  ILIKE CONCAT('%', :busqueda, '%')  ) )
                             AND ( (:estatus IS NULL) OR (e.estatus = :estatus) )
                             AND ( (:idTipoCliente IS NULL) OR (tipoCliente.idTipoCliente = :idTipoCliente) )
                ORDER BY e.alias ASC
            """)
    List<Cliente> findAllByFilter(@Param("busqueda") String busqueda, @Param("estatus") Integer estatus, @Param("idTipoCliente") Integer idTipoCliente);

}