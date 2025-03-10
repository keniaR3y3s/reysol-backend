package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Usuario;
import com.revoktek.reysol.persistence.entities.UsuarioRol;
import com.revoktek.reysol.persistence.entities.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    @Query("""
            SELECT
                usuarioRol
            FROM UsuarioRol usuarioRol
            INNER JOIN FETCH usuarioRol.usuario usuario
            INNER JOIN FETCH usuarioRol.rol rol
            INNER JOIN FETCH usuario.empleado empleado
            WHERE
                rol.idRol = :idRol
            ORDER BY empleado.nombre ASC
            """)
    List<UsuarioRol> findByRol(
            @Param("idRol") Long idRol
    );

    List<UsuarioRol> findAllByUsuario(Usuario usuario);
}