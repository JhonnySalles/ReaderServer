# ReaderServer API

Esta é a API de comunicação para arquivos de biblioteca, informações de bookmark, OPF e ComicInfo para aplicativos e desktop. Baseada em uma arquitetura robusta e genérica com Spring Boot e Kotlin.

## 🚀 Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **Framework:** Spring Boot 3.x
- **Segurança:** Spring Security com autenticação JWT
- **Persistência:** Spring Data JPA, Hibernate
- **Banco de Dados:** H2 (padrão para desenvolvimento/teste) / Suporte a Bancos Relacionais
- **Migração de Banco de Dados:** Flyway
- **Documentação de API:** Swagger / OpenAPI 3
- **Estrutura de Dados:** HATEOAS
- **Formatos Suportados:** JSON, XML, YAML

## ⚙️ Arquitetura e Funcionalidades

O projeto possui uma arquitetura baseada em classes genéricas, facilitando a criação de novos recursos e a manutenção do código:
- **`GenericJpaController`**: Fornece operações CRUD padrão (GET, POST, PUT, DELETE, Paginação) com suporte a HATEOAS.
- **`GenericJpaService`**: Camada de serviço genérica que lida com a lógica de persistência e validação básica.
- **Autenticação:** Baseada em Tokens JWT, permitindo login (`signin`) e renovação de token (`refresh`).

## 📚 Endpoints Principais

A documentação completa da API (Swagger UI) pode ser acessada após iniciar o servidor, através de:
`http://localhost:8080/swagger-ui.html` ou `http://localhost:8080/v3/api-docs`

### Autenticação (`/auth`)
* `POST /auth/signin`: Autentica um usuário e retorna o Token JWT.
* `PUT /auth/refresh/{username}`: Renova o Token JWT.

### Comic Info (`/api/comic-info/v1`)
Endpoint para gerenciar os metadados de quadrinhos (ComicInfo). Como herda de `GenericJpaController`, possui os seguintes métodos:
* `GET /api/comic-info/v1`: Lista todos com paginação.
* `GET /api/comic-info/v1/{id}`: Busca um registro específico pelo ID.
* `POST /api/comic-info/v1`: Cria um novo registro.
* `PUT /api/comic-info/v1`: Atualiza um registro existente.
* `DELETE /api/comic-info/v1/{id}`: Exclui um registro.

## 🛠️ Como Executar

### Pré-requisitos
* Java 17 ou superior
* Maven

### Passos
1. Clone o repositório ou navegue até o diretório do projeto.
2. Acesse a pasta `server`:
   ```bash
   cd server
   ```
3. Compile e rode a aplicação:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
4. A API estará rodando em `http://localhost:8080`.

## 📦 Estrutura de Diretórios
* `server/src/main/kotlin/.../readerserver/controller`: Controladores da API.
* `server/src/main/kotlin/.../readerserver/service`: Serviços e regras de negócio.
* `server/src/main/kotlin/.../readerserver/model`: Entidades (JPA) e DTOs (Data Transfer Objects).
* `server/src/main/kotlin/.../readerserver/repository`: Interfaces de repositório Spring Data.
* `server/src/main/kotlin/.../readerserver/security`: Configuração de segurança e provedor JWT.
* `server/src/main/kotlin/.../readerserver/config`: Configurações globais (Web, Swagger, etc).
* `server/src/main/resources/db/migration`: Scripts SQL de migração do Flyway.

## 🛡️ Segurança

Todas as rotas sob `/api/**` exigem autenticação via cabeçalho `Authorization: Bearer <token>`, exceto aquelas explicitamente liberadas como os endpoints do Swagger e a rota `/auth/**`.
