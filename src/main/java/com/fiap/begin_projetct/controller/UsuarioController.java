package com.fiap.begin_projetct.controller;

import com.fiap.begin_projetct.model.Usuario;
import com.fiap.begin_projetct.service.UsuarioService;
import com.fiap.begin_projetct.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "API para gerenciamento de usuários e autenticação")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    private final SessionService sessionService;
    
    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuário por email", description = "Retorna um usuário específico pelo seu email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cadastra um novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existe")
    })
    public ResponseEntity<com.fiap.begin_projetct.model.Usuario> criar(@Valid @RequestBody com.fiap.begin_projetct.dto.UsuarioRequest request) {
        com.fiap.begin_projetct.model.Usuario novoUsuario = usuarioService.salvar(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }
    
    
    
    @PostMapping("/logout")
    @Operation(summary = "Realizar logout", description = "Desativa a sessão do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso")
    })
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> logoutRequest) {
        String email = logoutRequest.get("email");
        sessionService.logout(email);
        
        return ResponseEntity.ok(Map.of(
            "message", "Logout realizado com sucesso"
        ));
    }
    
    @PostMapping("/validar-sessao")
    @Operation(summary = "Validar sessão", description = "Verifica se a sessão do usuário está ativa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessão validada com sucesso")
    })
    public ResponseEntity<Map<String, Object>> validarSessao(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean sessaoValida = sessionService.validarSessao(email);
        
        return ResponseEntity.ok(Map.of(
            "valid", sessaoValida,
            "message", sessaoValida ? "Sessão ativa" : "Sessão inválida ou expirada"
        ));
    }
    
    @PostMapping("/limpar-sessoes-expiradas")
    @Operation(summary = "Limpar sessões expiradas", description = "Remove todas as sessões expiradas do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Limpeza realizada com sucesso")
    })
    public ResponseEntity<Map<String, String>> limparSessoesExpiradas() {
        sessionService.limparSessoesExpiradas();
        
        return ResponseEntity.ok(Map.of(
            "message", "Sessões expiradas limpas com sucesso"
        ));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<com.fiap.begin_projetct.model.Usuario> atualizar(@PathVariable Long id, @Valid @RequestBody com.fiap.begin_projetct.dto.UsuarioRequest request) {
        com.fiap.begin_projetct.model.Usuario usuarioAtualizado = usuarioService.atualizar(id, request.toEntity());
        return ResponseEntity.ok(usuarioAtualizado);
    }
    
    @PutMapping("/{id}/ativar-sessao")
    @Operation(summary = "Ativar sessão do usuário", description = "Ativa a sessão de um usuário específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessão ativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Map<String, String>> ativarSessao(@PathVariable Long id) {
        usuarioService.ativarSessao(id);
        
        return ResponseEntity.ok(Map.of(
            "message", "Sessão ativada com sucesso"
        ));
    }
    
    @PutMapping("/{id}/desativar-sessao")
    @Operation(summary = "Desativar sessão do usuário", description = "Desativa a sessão de um usuário específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessão desativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Map<String, String>> desativarSessao(@PathVariable Long id) {
        usuarioService.desativarSessao(id);
        
        return ResponseEntity.ok(Map.of(
            "message", "Sessão desativada com sucesso"
        ));
    }
    
    @GetMapping("/verificar-email/{email}")
    @Operation(summary = "Verificar existência de email", description = "Verifica se um email já está cadastrado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    })
    public ResponseEntity<Map<String, Boolean>> verificarEmail(@PathVariable String email) {
        boolean exists = usuarioService.existsByEmail(email);
        
        return ResponseEntity.ok(Map.of(
            "exists", exists
        ));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
