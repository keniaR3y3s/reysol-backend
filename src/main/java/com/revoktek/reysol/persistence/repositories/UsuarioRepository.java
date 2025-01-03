package com.revoktek.reysol.persistence.repositories;

import com.revoktek.reysol.persistence.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);


    Boolean existsByUsuario(String usuario);

    Optional<Usuario> findByUsuarioAndIdUsuarioNot(String usuario, Long idUsuario);

    Usuario findByIdUsuario(Long idUsuario);
}
