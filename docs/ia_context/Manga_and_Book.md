# Contexto da IA: Manga & Book

## 1. Visão Geral
Os módulos **Manga** e **Book** são responsáveis por gerenciar os registros dos arquivos físicos de leitura do sistema:
- **Manga**: Representa arquivos de histórias em quadrinhos / mangás (ex: extensões `.cbz`, `.cbr`, `.pdf`, etc.).
- **Book**: Representa arquivos de livros digitais (ex: extensões `.epub`, `.pdf`, `.azw3`, `.mobi`, etc.).

Ambos são associados às suas respectivas entidades de metadados (`ComicInfo` e `Opf`), garantindo que tanto a referência aos arquivos físicos quanto os metadados bibliográficos possam ser recuperados de forma direta.

---

## 2. Estrutura do Banco de Dados

### Tabela `manga`
- `id` (VARCHAR(36), PK): UUID gerado automaticamente.
- `nome` (VARCHAR(500)): Nome descritivo ou de exibição da obra / volume.
- `file_name` (VARCHAR(500)): Nome real do arquivo no disco.
- `extension` (VARCHAR(10)): Extensão do arquivo (ex: `cbz`, `cbr`).
- `file_date` (DATETIME): Data de modificação do arquivo no disco.
- `comicinfo_id` (VARCHAR(36), FK): Chave estrangeira para a tabela `comicinfo`.
- `atualizacao` (TIMESTAMP): Atualizado automaticamente pelo banco via trigger/default (`ON UPDATE CURRENT_TIMESTAMP`). Não mapeado no JPA.

### Tabela `book`
- `id` (VARCHAR(36), PK): UUID gerado automaticamente.
- `nome` (VARCHAR(500)): Nome descritivo ou de exibição do livro.
- `file_name` (VARCHAR(500)): Nome real do arquivo no disco.
- `extension` (VARCHAR(10)): Extensão do arquivo (ex: `epub`, `pdf`).
- `file_date` (DATETIME): Data de modificação do arquivo no disco.
- `opf_id` (VARCHAR(36), FK): Chave estrangeira para a tabela `opf`.
- `atualizacao` (TIMESTAMP): Atualizado automaticamente pelo banco via trigger/default (`ON UPDATE CURRENT_TIMESTAMP`). Não mapeado no JPA.

---

## 3. Endpoints Disponíveis

### Manga (`/api/manga`)
- `GET /api/manga` - Listar todos os registros de mangá.
- `GET /api/manga/page` - Listagem paginada HATEOAS.
- `GET /api/manga/{id}` - Obter dados de um mangá (incluindo o objeto `comicInfo` completo vinculado).
- `POST /api/manga` - Criar novo registro de mangá.
- `PUT /api/manga/{id}` - Atualizar registro completo.
- `PATCH /api/manga/{id}` - Atualizar campos parciais.
- `DELETE /api/manga/{id}` - Deletar registro de mangá.
- `GET /api/manga/search/nome?nome={texto}` - Busca paginada por nome.
- `GET /api/manga/search/file-name?fileName={texto}` - Busca paginada por nome de arquivo.
- `GET /api/manga/comicinfo/{comicInfoId}` - Lista de mangás vinculados a um ComicInfo.
- `GET /api/manga/comicinfo/{comicInfoId}/page` - Listagem paginada por ComicInfo ID.

### Book (`/api/book`)
- `GET /api/book` - Listar todos os registros de livros.
- `GET /api/book/page` - Listagem paginada HATEOAS.
- `GET /api/book/{id}` - Obter dados de um livro (incluindo o objeto `opf` completo vinculado).
- `POST /api/book` - Criar novo registro de livro.
- `PUT /api/book/{id}` - Atualizar registro completo.
- `PATCH /api/book/{id}` - Atualizar campos parciais.
- `DELETE /api/book/{id}` - Deletar registro de livro.
- `GET /api/book/search/nome?nome={texto}` - Busca paginada por nome.
- `GET /api/book/search/file-name?fileName={texto}` - Busca paginada por nome de arquivo.
- `GET /api/book/opf/{opfId}` - Lista de livros vinculados a um OPF.
- `GET /api/book/opf/{opfId}/page` - Listagem paginada por OPF ID.
