-- Inserir usuários de teste para validação dos casos de teste
-- Este script complementa o usuário admin já criado em V2

-- Usuário admin (já existe em V2, mas incluído aqui para referência)
-- Email: admin@careplus.com
-- Senha: Admin@123
-- Hash: $2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.

-- Inserir usuário regular para testes
INSERT INTO usuarios (email, senha, sessao_ativa) 
VALUES ('usuario@careplus.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', FALSE);

-- Inserir usuário para testes de validação de senha
INSERT INTO usuarios (email, senha, sessao_ativa) 
VALUES ('testsenha@careplus.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', FALSE);

-- Inserir usuário para testes de expiração de sessão
INSERT INTO usuarios (email, senha, sessao_ativa, ultimo_acesso) 
VALUES ('sessao@careplus.com', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', TRUE, DATE_SUB(NOW(), INTERVAL 2 MINUTE));

-- Comentários sobre as senhas:
-- usuario@careplus.com: senha = "password" (hash com BCrypt)
-- testsenha@careplus.com: senha = "Test@1234" (hash com BCrypt)
-- sessao@careplus.com: senha = "Admin@123" (hash com BCrypt) - sessão ativa há 2 minutos para testes de expiração

-- Inserir usuário com sessão expirada para testes
INSERT INTO usuarios (email, senha, sessao_ativa, ultimo_acesso) 
VALUES ('expirado@careplus.com', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', TRUE, DATE_SUB(NOW(), INTERVAL 6 MINUTE));

-- expirado@careplus.com: senha = "Admin@123" (hash com BCrypt) - sessão expirada (>5 minutos)
