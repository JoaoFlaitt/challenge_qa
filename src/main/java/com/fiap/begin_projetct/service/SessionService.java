package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.infra.security.TokenService;
import com.fiap.begin_projetct.model.Usuario;
import com.fiap.begin_projetct.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public boolean validarSessao(String email) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> {
                    if (!usuario.getSessaoAtiva() || usuario.getUltimoAcesso() == null) {
                        return false;
                    }

                    // Verificar expiração da sessão (30 minutos de inatividade)
                    LocalDateTime limiteExpiracao = usuario.getUltimoAcesso().plusMinutes(30);
                    if (LocalDateTime.now().isAfter(limiteExpiracao)) {
                        usuarioRepository.invalidarSessao(usuario.getId());
                        return false;
                    }

                    // Atualizar último acesso
                    usuarioRepository.atualizarUltimoAcesso(usuario.getId(), LocalDateTime.now());
                    return true;
                })
                .orElse(false);
    }

    public void logout(String email) {
        usuarioRepository.findByEmail(email)
                .ifPresent(usuario -> usuarioRepository.invalidarSessao(usuario.getId()));
    }

    public void limparSessoesExpiradas() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(30);
        List<Usuario> sessoesExpiradas = usuarioRepository.findSessoesInativas(limite);
        
        sessoesExpiradas.forEach(usuario -> 
            usuarioRepository.invalidarSessao(usuario.getId()));
    }
}
