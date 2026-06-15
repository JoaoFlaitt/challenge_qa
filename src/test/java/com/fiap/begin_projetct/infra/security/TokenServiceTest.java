package com.fiap.begin_projetct.infra.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret-key");
    }

    @Test
    void deveGerarTokenValido() {
        String login = "test@example.com";
        String token = tokenService.gerarToken(login);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void deveExtrairSubjectDoToken() {
        String login = "test@example.com";
        String token = tokenService.gerarToken(login);
        
        String subject = tokenService.getSubject(token);
        
        assertEquals(login, subject);
    }

    @Test
    void deveLancarExcecaoParaTokenInvalido() {
        String tokenInvalido = "token-invalido";
        
        assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(tokenInvalido);
        });
    }
}
