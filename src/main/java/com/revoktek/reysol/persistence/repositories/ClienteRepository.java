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
                INNER JOIN e.tipoCliente tipoCliente
                WHERE tipoCliente.idTipoCliente = :idTipoCliente AND ( (:busqueda IS NULL) OR (
                   CONCAT(e.nombre, ' ', e.primerApellido, ' ', COALESCE(e.segundoApellido, ''))
                   ILIKE CONCAT('%', :busqueda, '%')
                 ) OR ( e.alias  ILIKE CONCAT('%', :busqueda, '%')  ) )
                ORDER BY e.nombre ASC
            """)
    List<Cliente> findAllByFilter(@Param("busqueda") String busqueda, @Param("idTipoCliente") Integer idTipoCliente);}