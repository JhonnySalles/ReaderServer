# Visão Geral da Arquitetura - ReaderServer API

## 🎯 Objetivo / Contexto
O `ReaderServer` é uma API backend em Kotlin (Spring Boot 3) cujo principal objetivo é servir como central de comunicação e sincronização de dados de biblioteca, informações de bookmark, OPF e ComicInfo para aplicativos e interfaces desktop. Baseado na estrutura do `ApiIntegracao`, ele provê suporte a operações CRUD genéricas, serialização multi-formato e segurança JWT.

## 🧩 Estrutura de Diretórios e Padrão
A estrutura de pacotes em `src/main/kotlin/br/com/fenix/readerserver` é organizada por responsabilidades:

- **`controller/`**: Endpoints REST, incluindo controladores genéricos (`GenericJpaController`) e específicos (como `AuthController`, `ComicInfoController`).
- **`service/`**: Lógica de negócio, contendo o serviço genérico base (`GenericJpaService`) e os serviços de domínio (`AuthService`, `ComicInfoService`).
- **`repository/`**: Interfaces `JpaRepository` do Spring Data para as entidades.
- **`model/`**: Entidades mapeadas para o banco de dados via JPA e os `DTOs` (Data Transfer Objects) usados para requisições e respostas.
- **`config/`**: Configurações de aplicação, Swagger/OpenAPI, Web (Content Negotiation) e Segurança.
- **`security/`**: Gerenciamento de tokens JWT e filtros de autenticação (`JwtTokenProvider`, `JwtTokenFilter`).

## ⚙️ Tecnologias e Integrações (Core)
- **Linguagem:** Kotlin 1.9+ (JVM 17)
- **Framework Web:** Spring Boot 3.x (Spring Web, Spring Security, Spring HATEOAS, Spring Data JPA)
- **Banco de Dados:** H2 (padrão) / Suporte a SGBDs Relacionais (PostgreSQL/MySQL) via JPA
- **Autenticação:** JWT via pacote `java-jwt` (Auth0) + BCrypt
- **Formatos de Dados:** JSON, XML (`jackson-dataformat-xml`), YAML (`jackson-dataformat-yaml`)
- **Documentação API:** SpringDoc OpenAPI 3 (Swagger UI)
- **Migrações de Banco:** Flyway

## 🔄 Fluxo de Processamento Padrão
1. **Autenticação**: O cliente faz `POST` em `/auth/signin` com credenciais e recebe um JWT token.
2. **Requisição Protegida**: Nas requisições para `/api/**`, o cabeçalho `Authorization: Bearer <token>` é exigido.
3. **Filtro JWT**: `JwtTokenFilter` intercepta o request, valida o token via `JwtTokenProvider` e estabelece o `SecurityContext`.
4. **Execução Genérica**: O `GenericJpaController` direciona chamadas CRUD padrão para o `GenericJpaService`, lidando com conversão de DTOs e entidades.
5. **Retorno HATEOAS**: A resposta é paginada quando apropriado, encapsulada em `PagedModel` com links HATEOAS e formatada no `MediaType` solicitado (JSON, XML ou YAML).
