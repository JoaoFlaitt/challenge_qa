-- Corrigir tamanho do campo senha para acomodar hash BCrypt (60 caracteres)
-- BCrypt hashes sempre têm 60 caracteres

-- Alterar o tamanho da coluna senha
ALTER TABLE usuarios MODIFY COLUMN senha VARCHAR(255) NOT NULL;

-- Comentário: BCrypt hashes têm exatamente 60 caracteres, mas usamos VARCHAR(255) para segurança futura
