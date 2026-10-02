# Autenticação e Segurança - Auth

## 🎯 Objetivo / Contexto
Gerenciar o controle de acesso à API `ReaderServer`. Apenas clientes autorizados e autenticados podem interagir com os dados das bibliotecas e marcadores. Baseado no padrão de JSON Web Tokens (JWT).

## 🧩 Arquivos e Componentes
- **Controlador:** `br.com.fenix.readerserver.controller.AuthController`
- **Serviço de Autenticação:** `br.com.fenix.readerserver.service.AuthService`
- **Provedor JWT:** `br.com.fenix.readerserver.security.JwtTokenProvider`
- **Filtro Spring Security:** `br.com.fenix.readerserver.security.JwtTokenFilter`
- **Modelos:** `AccountCredentialsDto`, `TokenDto`

## ⚙️ Regras de Negócio e Lógica (Core Logic)
- **Token JWT:**
  - Emissão feita pela biblioteca `java-jwt` da Auth0.
  - O `JwtTokenProvider` contém lógica de codificação e assinatura secreta gerada com HMAC256.
  - Implementa suporte a "Refresh Tokens" para que o cliente atualize seu token principal antes de expirar sem necessitar enviar as credenciais novamente.
- **Filtro Interceptador (`JwtTokenFilter`):**
  - Captura toda requisição de entrada. Se a URI da requisição exigir autenticação e contiver o header `Authorization: Bearer <token>`, o token será desmembrado e validado.
  - Em caso de sucesso, seta o objeto `Authentication` no `SecurityContextHolder`.
- **Configurações de Segurança:**
  - Sessão definida como Stateless, pois quem gerencia o estado da autenticação é o próprio token fornecido nas chamadas HTTP.
  - O acesso ao endpoint `/auth/**` e à documentação do Swagger UI (`/swagger-ui/**`, `/v3/api-docs/**`) são públicos. Rotas de `/api/**` exigem token válido.

## 🔄 Endpoints

- **`POST /auth/signin`**
  - **Corpo:** `{ "username": "...", "password": "..." }`
  - **Resposta:** `TokenDto` contendo `accessToken`, `refreshToken`, e datas de criação/expiração.
  - **Uso:** Primeiro acesso do cliente. Valida o hash da senha no banco de dados.

- **`PUT /auth/refresh/{username}`**
  - **Header:** `Authorization: Bearer <refreshToken>`
  - **Resposta:** Novo `TokenDto`.
  - **Uso:** Solicitação de um novo par de tokens usando um refresh token válido.
