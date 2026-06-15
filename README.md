# 🏥 Care Plus API

Sistema de gerenciamento de dietas e nutrição com autenticação JWT, integração com API externa de alimentos (USDA FoodData Central) e documentação Swagger.

---

## 👥 Equipe

| Nome | RM |
| :--- | :--- |
| Gabriel Souza Fiore | RM553710 |
| Guilherme Santiago | RM552321 |
| Miguel Leal Tasso | RM553009 |
| João Víctor Seixas | RM553888 |
| Lucca Enrico | RM553678 |

---

## 🚀 Tecnologias Utilizadas

| Componente | Tecnologia | Versão |
| :--- | :--- | :--- |
| Linguagem | Java | 21 |
| Framework | Spring Boot | 3.4.1 |
| Banco de Dados | MySQL | 8.0 |
| Segurança | Spring Security + JWT (auth0) | 4.5.1 |
| Documentação | Swagger UI / OpenAPI | 3.0 (springdoc 2.7.0) |
| Migrações de Banco | Flyway | Incluso no Spring Boot 3.x |
| Ferramenta de Build | Maven | 3.x |
| Boilerplate | Lombok | - |
| HTTP Reativo | Spring WebFlux / WebClient | - |

---

## 📋 Pré-requisitos

- **JDK 21** instalado e configurado no `PATH`
- **MySQL 8.0** rodando localmente
- **Maven** (ou usar o `mvnw` incluído no projeto)
- Banco de dados `care_plus_db` criado no MySQL

---

## ⚙️ Configuração do Banco de Dados

### 1. Criar o banco de dados

Conecte-se ao MySQL e execute:

```sql
CREATE DATABASE care_plus_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configuração no `application.properties`

O arquivo já está configurado com os seguintes parâmetros:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/care_plus_db
spring.datasource.username=root
spring.datasource.password=0000
```

> ⚠️ Se sua senha do MySQL for diferente, altere `spring.datasource.password` antes de rodar.

### 3. Migrações com Flyway (automático)

O projeto usa **Flyway** para controle de versão do banco de dados. As migrações rodam **automaticamente** ao iniciar a aplicação — não é necessário executar nenhum SQL manualmente.

As migrações estão em: `src/main/resources/db/migration/`

| Arquivo | Descrição |
| :--- | :--- |
| `V1__init.sql` | Cria a tabela `pacientes` |
| `V2__create_usuarios_table.sql` | Cria a tabela `usuarios` |
| `V3__insert_test_users.sql` | Insere usuários de teste |
| `V4__fix_senha_column_size.sql` | Ajusta tamanho da coluna senha |
| `V5__super_inserts.sql` | Insere dados iniciais |

> ✅ Na **primeira execução**, o Flyway detecta o banco existente automaticamente graças ao `baseline-on-migrate=true`.

---

## ▶️ Como Rodar o Projeto

### Windows (PowerShell)

```powershell
$env:JWT_TOKEN="care-plus-secret-key-2024"; .\mvnw.cmd spring-boot:run
```

### Linux / macOS (Terminal)

```bash
JWT_TOKEN="care-plus-secret-key-2024" ./mvnw spring-boot:run
```

> A aplicação inicia na porta **8080**.

---

## 📖 Documentação Interativa (Swagger)

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

Todos os endpoints estão documentados com descrições, parâmetros e exemplos de resposta.

---

## 🔐 Autenticação JWT

### 1. Fazer login

```http
POST http://localhost:8080/login
Content-Type: application/json

{
  "login": "admin@careplus.com",
  "senha": "admin123"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. Usar o token nas requisições protegidas

Adicione o cabeçalho em todas as requisições:

```
Authorization: Bearer <seu_token_aqui>
```

---

## 📡 Principais Endpoints

### 👤 Pacientes

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/pacientes` | Listar todos os pacientes |
| `GET` | `/api/pacientes/{id}` | Buscar paciente por ID |
| `GET` | `/api/pacientes/cpf/{cpf}` | Buscar paciente por CPF |
| `POST` | `/api/pacientes` | Cadastrar novo paciente |
| `PUT` | `/api/pacientes/{id}` | Atualizar paciente |
| `DELETE` | `/api/pacientes/{id}` | Remover paciente |

**Exemplo de requisição POST `/api/pacientes`:**
```json
{
  "nome": "Maria Silva",
  "cpf": "123.456.789-00",
  "email": "maria@email.com",
  "telefone": "(11) 99999-9999",
  "dataNascimento": "1990-05-15",
  "convenio": "Unimed"
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "nome": "Maria Silva",
  "cpf": "123.456.789-00",
  "email": "maria@email.com",
  "telefone": "(11) 99999-9999",
  "dataNascimento": "1990-05-15",
  "convenio": "Unimed",
  "createdAt": "2026-05-07T15:00:00"
}
```

---

### 🍎 Alimentos (com integração USDA)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/foods` | Listar todos os alimentos |
| `GET` | `/api/foods/buscar?nome=frango` | Buscar por nome (banco local + USDA) |
| `GET` | `/api/foods/{id}` | Buscar alimento por ID |
| `POST` | `/api/foods` | Cadastrar alimento manualmente |
| `PUT` | `/api/foods/{id}` | Atualizar dados nutricionais |
| `DELETE` | `/api/foods/{id}` | Remover alimento |

**Exemplo de requisição POST `/api/foods`:**
```json
{
  "name": "Frango Grelhado",
  "caloriesPer100g": 165,
  "proteins": 31.0,
  "carbs": 0.0,
  "fats": 3.6,
  "fiber": 0.0,
  "sodium": 0.074,
  "sugar": 0.0,
  "servingSize": 100.0,
  "servingUnit": "g"
}
```

---

### 🥗 Planos Alimentares

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/diet-plans` | Listar todos os planos |
| `GET` | `/api/diet-plans/paciente/{id}` | Planos de um paciente |
| `POST` | `/api/diet-plans/paciente/{id}` | Criar plano para um paciente |
| `PUT` | `/api/diet-plans/{id}` | Atualizar plano |
| `DELETE` | `/api/diet-plans/{id}` | Remover plano |

---

### 📝 Registro de Consumo (Food Logs)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/food-logs/paciente/{id}` | Histórico de consumo |
| `GET` | `/api/food-logs/paciente/{id}/calorias` | Total de calorias por data |
| `POST` | `/api/food-logs/paciente/{id}` | Registrar consumo |
| `DELETE` | `/api/food-logs/{id}` | Remover registro |

---

## 🍽️ Integração FoodData Central (USDA)

A busca de alimentos funciona de forma **híbrida**:

1. Consulta o banco local primeiro
2. Se menos de 5 resultados → consulta automaticamente a **API USDA**
3. Resultados da API são **cacheados** no banco local (campo `source = 'API'`)
4. Duplicatas evitadas via campo `fdc_id`

Para habilitar, configure em `application.properties`:

```properties
fooddata.api.key=SUA_CHAVE_AQUI
```

Obtenha sua chave gratuita em: https://fdc.nal.usda.gov/api-key-signup.html

---

## 🗂️ Estrutura do Projeto

```
src/main/java/com/fiap/begin_projetct/
├── controller/     # Endpoints REST (HTTP)
├── service/        # Lógica de negócio
├── repository/     # Acesso ao banco de dados (Spring Data JPA)
├── model/          # Entidades JPA (tabelas do banco)
├── dto/            # Data Transfer Objects (validação de entrada)
├── exception/      # Tratamento global de erros (@RestControllerAdvice)
└── infra/
    ├── security/   # JWT, filtros e configurações de segurança
    └── config/     # Configurações gerais do Spring
```

---

## 📐 Arquitetura e Diagramas

Consulte o arquivo **`architecture.md`** para:
- Diagrama de camadas da arquitetura
- Diagrama ER (Entidade-Relacionamento)
- Diagrama UML de Classes (imagem: `uml-classes.png`)
- Fluxo de autenticação JWT
- Tabela completa de endpoints

---

## ⚠️ Observações Importantes

- O Hibernate está configurado com `ddl-auto=validate` — o **Flyway é responsável** por criar e versionar o banco
- A variável de ambiente `JWT_TOKEN` é obrigatória para gerar tokens de autenticação
- O Swagger não exige autenticação para ser acessado
- Logs de debug estão ativados — normal ver SQL no console durante desenvolvimento