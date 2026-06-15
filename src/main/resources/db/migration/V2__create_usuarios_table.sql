-- Criar tabela de usuários para autenticação
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(60) NOT NULL,
    ultimo_acesso DATETIME,
    token_expiracao DATETIME,
    sessao_ativa BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_sessao_ativa (sessao_ativa),
    INDEX idx_ultimo_acesso (ultimo_acesso)
);

-- Inserir usuário administrador padrão para testes
INSERT IGNORE INTO usuarios (email, senha, sessao_ativa) 
VALUES ('admin@careplus.com', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', FALSE);

-- Comentário sobre a senha: a senha é "Admin@123" (hash com BCrypt)
