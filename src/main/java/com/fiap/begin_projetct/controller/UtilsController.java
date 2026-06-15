package com.fiap.begin_projetct.controller;

import com.fiap.begin_projetct.dto.BcryptRequest;
import com.fiap.begin_projetct.dto.BcryptResponse;
import com.fiap.begin_projetct.model.Usuario;
import com.fiap.begin_projetct.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/utils")
@RequiredArgsConstructor
@Tag(name = "Utilitários", description = "Endpoints utilitários para desenvolvimento e testes")
public class UtilsController {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;

    @Autowired
    private com.fiap.begin_projetct.service.SQLRunnerService sqlRunnerService;

    @PostMapping("/popular-banco")
    public org.springframework.http.ResponseEntity<String> popular() {
        sqlRunnerService.runInserts();
        return org.springframework.http.ResponseEntity.ok("Banco populado com sucesso!");
    }

    @PostMapping("/bcrypt")
    @Operation(summary = "Gerar hash BCrypt", description = "Recebe uma senha em texto plano e retorna o hash BCrypt correspondente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hash gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Senha não informada")
    })
    public ResponseEntity<BcryptResponse> gerarHashBcrypt(@RequestBody BcryptRequest request) {
        if (request.getSenha() == null || request.getSenha().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        
        String hash = passwordEncoder.encode(request.getSenha());
        return ResponseEntity.ok(new BcryptResponse(request.getSenha(), hash));
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar usuário completo", description = "Registra um novo usuário com email e senha (a senha será automaticamente criptografada)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existe")
    })
    public ResponseEntity<Map<String, Object>> registrarUsuario(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String senha = request.get("senha");

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 8 caracteres");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);

        Usuario salvo = usuarioService.salvar(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "id", salvo.getId(),
            "email", salvo.getEmail(),
            "message", "Usuário registrado com sucesso"
        ));
    }
}
