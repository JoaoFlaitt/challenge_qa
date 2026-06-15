package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.infra.security.TokenService;
import com.fiap.begin_projetct.model.Usuario;
import com.fiap.begin_projetct.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    
    /**
     * Listar todos os usuários
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    /**
     * Buscar usuário por ID
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    /**
     * Buscar usuário por email
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    /**
     * Criar novo usuário
     */
    public Usuario salvar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Usuário com email '" + usuario.getEmail() + "' já existe");
        }
        
        // Criptografar senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Atualizar usuário
     */
    public Usuario atualizar(Long id, Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado com ID: " + id);
        }
        
        Usuario usuarioAtualizado = usuarioExistente.get();
        
        // Verificar se o email está sendo alterado e se já existe
        if (!usuarioAtualizado.getEmail().equals(usuario.getEmail()) && 
            usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Usuário com email '" + usuario.getEmail() + "' já existe");
        }
        
        usuarioAtualizado.setEmail(usuario.getEmail());
        
        // Atualizar senha apenas se fornecida
        if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
            usuarioAtualizado.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        
        return usuarioRepository.save(usuarioAtualizado);
    }
    
    /**
     * Deletar usuário
     */
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    /**
     * Autenticar usuário
     */
    public String autenticar(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }
        
        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }
        
        // Atualizar último acesso e ativar sessão
        usuarioRepository.atualizarUltimoAcesso(usuario.getId(), LocalDateTime.now());
        
        // Gerar token JWT
        return tokenService.gerarToken(usuario.getEmail());
    }
    
    /**
     * Ativar sessão do usuário
     */
    public void ativarSessao(Long usuarioId) {
        usuarioRepository.atualizarUltimoAcesso(usuarioId, LocalDateTime.now());
    }
    
    /**
     * Desativar sessão do usuário (logout)
     */
    public void desativarSessao(Long usuarioId) {
        usuarioRepository.invalidarSessao(usuarioId);
    }
    
    /**
     * Verificar se usuário existe por email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
