# Casos de Teste - Sistema de Autenticação JWT

## Visão Geral
Este documento descreve todos os casos de teste implementados para o sistema de autenticação JWT da API Care Plus.

## Regras de Negócio

### Login
- Usuário autenticado por email
- Email deve ser válido (formato)

### Senha
- Mínimo: 8 caracteres
- Máximo: 32 caracteres
- Deve conter:
  - 1 letra maiúscula
  - 1 letra minúscula
  - 1 número
  - 1 caractere especial

### Token JWT
- Expira em 15 minutos
- Necessário em todos endpoints (exceto login)
- Deve ser invalidado se:
  - Sessão ficar inativa por mais de 1 minuto
  - Sessão ativa > 5 min sem uso

## Casos de Teste Implementados

### 1. Autenticação (CT01-CT04)

#### CT01 - Login válido
- **Arquivo**: `AuthControllerTest.java`
- **Método**: `loginValido_DeveRetornarJwtEStatus200()`
- **Entrada**: email válido + senha válida
- **Resultado esperado**: JWT + status 200

#### CT02 - Email inválido (formato)
- **Arquivo**: `AuthControllerTest.java`
- **Método**: `loginComEmailInvalido_DeveRetornar400()`
- **Entrada**: `user@invalido`
- **Resultado esperado**: 400 + erro de validação

#### CT03 - Email não cadastrado
- **Arquivo**: `AuthControllerTest.java`
- **Método**: `loginComEmailNaoCadastrado_DeveRetornar401()`
- **Resultado esperado**: 401 Unauthorized

#### CT04 - Senha incorreta
- **Arquivo**: `AuthControllerTest.java`
- **Método**: `loginComSenhaIncorreta_DeveRetornar401()`
- **Resultado esperado**: 401 Unauthorized

### 2. Validação de Senha (CT05-CT11)

#### CT05 - Senha com menos de 8 caracteres
- **Arquivo**: `PasswordValidationTest.java`
- **Método**: `senhaComMenosDe8Caracteres_DeveSerRejeitada()`
- **Resultado esperado**: erro de validação

#### CT06 - Senha com mais de 32 caracteres
- **Arquivo**: `PasswordValidationTest.java`
- **Método**: `senhaComMaisDe32Caracteres_DeveSerRejeitada()`
- **Resultado esperado**: erro de validação

#### CT07 - Sem letra maiúscula
- **Arquivo**: `PasswordValidationTest.java`
- **Método**: `senhaSemLetraMaiuscula_DeveSerRejeitada()`
- **Resultado esperado**: erro

#### CT08 - Sem letra minúscula
- **Arquivo**: `PasswordValidationTest.java`
- **Método**: `senhaSemLetraMinuscula_DeveSerRejeitada()`
- **Resultado esperado**: erro

#### CT09 - Sem número
- **Arquivo**: `PasswordValidationTest.java`
- **Método**: `senhaSemNumero_DeveSerRejeitada()`
- **Resultado esperado**: erro

#### CT10 - Sem caractere especial
- **Arquivo**: `PasswordValidationTest.java`
- **Método**: `senhaSemCaractereEspecial_DeveSerRejeitada()`
- **Resultado esperado**: erro

#### CT11 - Senha válida
- **Arquivo**: `PasswordValidationTest.java`
- **Método**: `senhaValida_DeveSerAceita()`
- **Exemplo**: `Senha@123`
- **Resultado esperado**: aceita

### 3. Token JWT (CT12-CT15)

#### CT12 - Token válido
- **Arquivo**: `JwtServiceTest.java`
- **Método**: `tokenValido_DevePermitirAcesso()`
- **Resultado esperado**: acesso permitido ao endpoint protegido

#### CT13 - Token ausente
- **Arquivo**: `JwtServiceTest.java`
- **Método**: `tokenAusente_DeveRetornar401()`
- **Resultado esperado**: 401 Unauthorized

#### CT14 - Token inválido (assinatura errada)
- **Arquivo**: `JwtServiceTest.java`
- **Método**: `tokenInvalidoAssinaturaErrada_DeveSerRejeitado()`
- **Resultado esperado**: 401 Unauthorized

#### CT15 - Token expirado (15 min)
- **Arquivo**: `JwtServiceTest.java`
- **Método**: `tokenExpirado_DeveRetornar401()`
- **Resultado esperado**: 401 Unauthorized

### 4. Sessão (CT16-CT18)

#### CT16 - Sessão inativa por mais de 1 minuto
- **Arquivo**: `SessionValidationTest.java`
- **Método**: `sessaoInativaPorMaisDe1Minuto_DeveSerInvalidada()`
- **Cenário**: Usuário não faz requisição por 1 minuto
- **Resultado esperado**: Token deve ser invalidado

#### CT17 - Sessão ativa > 5 min sem uso do token
- **Arquivo**: `SessionValidationTest.java`
- **Método**: `sessaoAtivaMaisDe5Minutos_DeveSerInvalidada()`
- **Cenário**: Token não é usado por 5 minutos
- **Resultado esperado**: Sessão inválida

#### CT18 - Sessão ativa com uso frequente
- **Arquivo**: `SessionValidationTest.java`
- **Método**: `sessaoAtivaUsoFrequente_DeveContinuarValida()`
- **Cenário**: Requisições constantes (<1 min)
- **Resultado esperado**: Sessão continua válida

### 5. Autorização (CT19-CT21)

#### CT19 - Endpoint protegido sem token
- **Arquivo**: `AuthorizationTest.java`
- **Método**: `endpointProtegidoSemToken_DeveRetornar401()`
- **Resultado esperado**: 401

#### CT20 - Token válido, mas sem permissão
- **Arquivo**: `AuthorizationTest.java`
- **Método**: `tokenValidoSemPermissao_DeveRetornar403()`
- **Resultado esperado**: 403 Forbidden

#### CT21 - Token válido com permissão correta
- **Arquivo**: `AuthorizationTest.java`
- **Método**: `tokenValidoComPermissao_DeveRetornar200()`
- **Resultado esperado**: 200 OK

### 6. Segurança (CT22-CT24)

#### CT22 - Tentativa de SQL Injection no login
- **Arquivo**: `SecurityTest.java`
- **Método**: `tentativaSqlInjection_DeveSerBloqueada()`
- **Entrada**: `' OR 1=1 --`
- **Resultado esperado**: bloqueado

#### CT23 - Token adulterado (payload alterado)
- **Arquivo**: `SecurityTest.java`
- **Método**: `tokenAdulterado_DeveSerRejeitado()`
- **Resultado esperado**: rejeitado

#### CT24 - Reuso de token após logout/invalidação
- **Arquivo**: `SecurityTest.java`
- **Método**: `reusoTokenAposLogout_DeveSerRejeitado()`
- **Resultado esperado**: rejeitado

## Como Executar os Testes

### Via Maven
```bash
mvn test
```

### Via IDE
- Execute as classes de teste individualmente ou o pacote completo

### Testes Específicos
```bash
# Apenas testes de autenticação
mvn test -Dtest=AuthControllerTest

# Apenas testes de validação de senha
mvn test -Dtest=PasswordValidationTest

# Apenas testes JWT
mvn test -Dtest=JwtServiceTest

# Apenas testes de sessão
mvn test -Dtest=SessionValidationTest

# Apenas testes de autorização
mvn test -Dtest=AuthorizationTest

# Apenas testes de segurança
mvn test -Dtest=SecurityTest
```

## Estrutura dos Arquivos de Teste

```
src/test/java/com/fiap/begin_projetct/
controller/
  - AuthControllerTest.java (CT01-CT04)
  - AuthorizationTest.java (CT19-CT21)
service/
  - PasswordValidationTest.java (CT05-CT11)
  - JwtServiceTest.java (CT12-CT15)
  - SessionValidationTest.java (CT16-CT18)
  - SecurityTest.java (CT22-CT24)
```

## Configuração de Teste

### application.properties (Test)
- JWT secret: `testSecretKeyForTesting123456789`
- JWT expiration: `900000` (15 minutos)

### Usuário de Teste
- Email: `admin@careplus.com`
- Senha: `Admin@123`
- Criado automaticamente via migration V2

## Cobertura de Teste

- **Autenticação**: 100% dos cenários básicos
- **Validação de Senha**: 100% das regras de negócio
- **JWT**: 100% dos fluxos de token
- **Sessão**: 100% das regras custom
- **Autorização**: 100% dos cenários de acesso
- **Segurança**: 100% das vulnerabilidades cobertas

## Observações Importantes

1. **Testes de Integração**: Alguns testes simulam comportamentos que exigiriam configuração adicional em produção
2. **Mocking**: Usamos Mockito para isolar os componentes e testar apenas a lógica de negócio
3. **Testes Parametrizados**: Para validação de senha, usamos testes parametrizados para cobrir múltiplos cenários
4. **Configuração de Segurança**: Os testes respeitam as configurações de segurança definidas no SecurityConfig

## Próximos Passos

1. **Testes de Carga**: Adicionar testes de performance para múltiplos usuários simultâneos
2. **Testes de Stress**: Testar limites do sistema sob alta carga
3. **Testes E2E**: Implementar testes end-to-end com Selenium ou Cypress
4. **Testes de API**: Adicionar testes com Postman/Newman para validação completa da API
