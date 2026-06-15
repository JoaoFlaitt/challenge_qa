package com.fiap.begin_projetct.repository;

import com.fiap.begin_projetct.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoAcesso = :ultimoAcesso, u.sessaoAtiva = true WHERE u.id = :usuarioId")
    void atualizarUltimoAcesso(@Param("usuarioId") Long usuarioId, @Param("ultimoAcesso") LocalDateTime ultimoAcesso);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.sessaoAtiva = false WHERE u.id = :usuarioId")
    void invalidarSessao(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT u FROM Usuario u WHERE u.sessaoAtiva = true AND u.ultimoAcesso < :limite")
    java.util.List<Usuario> findSessoesInativas(@Param("limite") LocalDateTime limite);
}
