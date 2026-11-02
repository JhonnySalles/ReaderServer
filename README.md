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
4. A API estará rodando em `http://localhost:8083` (ou porta configurada).

### 📥 Importador de Mídias (CLI)
Para importar mangas, quadrinhos e livros escaneando pastas e subpastas locais:
1. Acesse o diretório `scripts/importer` e execute o arquivo `run.bat` (ou `import.bat`).
2. Para extração de arquivos `.cbr` e `.rar`, certifique-se de ter o `rar.exe` no diretório `scripts/importer/` (ou `bin/` ou no `PATH` do sistema).
3. Alternativamente, via terminal:
   ```bash
   cd scripts/importer
   yarn start
   ```
4. Siga o assistente interativo selecionando o tipo de mídia (Mangá / Livro) e informando o caminho do diretório.

---

## ☁️ Hospedagem no Alwaysdata (Deploy em Produção)

A API do ReaderServer pode ser hospedada centralizadamente na nuvem Alwaysdata através do recurso **Programa do Usuário** (*User program*).

### 1. Criar o Banco PostgreSQL
1. No painel do Alwaysdata, acesse **Bancos de dados > PostgreSQL**.
2. Crie uma nova base de dados (ex: `suaconta_readerserver`) e um usuário com permissões.

### 2. Compilar o Projeto (API Backend + Site Frontend)
Execute o script único de compilação:
```bash
scripts\alwaysdata\build.bat
```
* **Backend:** O arquivo `ReaderServer-0.0.1-SNAPSHOT.jar` será gerado em `server/target/`.
* **Frontend:** Os arquivos estáticos e o `.htaccess` serão gerados em `frontend/dist/`.


### 4. Enviar os Arquivos para o Alwaysdata
Conecte-se via SFTP ou SSH ao Alwaysdata e configure as pastas:

- **API**: Crie o diretório `/home/suaconta/readerserver/`:
  - Envie o arquivo `ReaderServer-*.jar`.
  - Envie o script `scripts/alwaysdata/start.sh` e dê permissão: `chmod +x start.sh`.
- **Site**: Crie ou utilize o diretório `/home/suaconta/www/readerserver/` e envie o conteúdo da pasta `frontend/dist/`.

### 5. Configurar no Painel Alwaysdata

#### A. Site da Aplicação Web (Frontend na pasta www)
Se a sua conta Alwaysdata já possui o site padrão apontando para o diretório raiz `/home/suaconta/www/`, **nenhuma configuração adicional é necessária**! Ao enviar os arquivos para `/home/suaconta/www/readerserver/`, o site já responderá automaticamente em `https://suaconta.alwaysdata.net/readerserver/`.

*(Caso crie um site específico para o caminho `/readerserver`: defina o tipo como **Arquivos estáticos** com diretório raiz `/home/suaconta/www/readerserver`).*

#### B. Site da API (Programa do Usuário em /readerserver/api)
1. Acesse **Web > Sites > Adicionar um site**.
2. Defina o endereço compartilhado (ex: `suaconta.alwaysdata.net`).
3. No campo **Caminho (Path)**: `/readerserver/api`
4. Selecione o tipo: **Programa do usuário** (*User program*).
5. No campo **Comando**: `/home/suaconta/readerserver/start.sh`
6. No campo **Diretório de trabalho**: `/home/suaconta/readerserver`
7. Em **Variáveis de ambiente**:
   - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://postgresql-suaconta.alwaysdata.net/suaconta_readerserver`
   - `SPRING_DATASOURCE_USERNAME`: seu usuário do PostgreSQL no Alwaysdata
   - `SPRING_DATASOURCE_PASSWORD`: sua senha do PostgreSQL no Alwaysdata
   - `JWT_SECRET_KEY`: sua chave secreta forte para os tokens JWT
   - `CORS_ALLOWED_ORIGINS`: origens permitidas (ex: `*`)

---

## 📄 Logs no Alwaysdata

Sem utilizar containers Docker, os logs são organizados diretamente no sistema de arquivos do Alwaysdata onde cada aplicação reside:

- **Logs da API (Spring Boot)**:
  - Arquivo da aplicação: `/home/suaconta/readerserver/logs/server.log` (gerado automaticamente com rotação diária e limite de 10MB por arquivo).
  - Visualização em tempo real: Acesse via SSH e execute:
    ```bash
    tail -f /home/suaconta/readerserver/logs/server.log
    ```
- **Logs do Site (Servidor Web Nginx/Apache)**:
  - Registros de acesso e erros HTTP:
    `/home/suaconta/admin/logs/sites/[nome-do-site]/`
  - Também acessíveis diretamente na aba **Logs** do painel do Alwaysdata no respectivo site.


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
