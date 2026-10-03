# Data (Raw Files / XML & OPF Storage)

## 🎯 Objetivo / Contexto
O componente `Data` (`DataFile`) armazena os arquivos e conteúdos brutos (ex.: `comicinfo.xml`, `content.opf` ou outros XMLs/arquivos de metadados) vinculados às entidades principais `ComicInfo` e `Opf`.

## 🧩 Arquivos e Componentes
- **Controlador:** `br.com.fenix.readerserver.controller.data.DataFileController`
- **Serviço:** `br.com.fenix.readerserver.service.data.DataFileService`
- **Repositório:** `br.com.fenix.readerserver.repository.data.DataFileRepository`
- **Modelo/Entidade:** `br.com.fenix.readerserver.model.data.DataFile`
- **DTO:** `br.com.fenix.readerserver.dto.data.DataFileDto`

## ⚙️ Regras de Negócio e Lógica (Core Logic)
- **Vínculos:** Cada registro de `DataFile` pode conter chave estrangeira opcional apontando para `comicinfo_id` ou `opf_id`.
- **Armazenamento:** O campo `file_content` armazena o texto puro (`LONGTEXT`).
- **Consultas Especializadas:** Possibilidade de recuperar todos os arquivos de um `ComicInfo` ou `Opf`, consultar paginado e baixar diretamente o conteúdo puro (`/raw`).
- **Auditoria no Banco:** O campo `atualizacao` é atualizado automaticamente pelo banco de dados.

## 🔄 Endpoints Principais
Base: `/api/data` (requer autenticação JWT via `Authorization: Bearer <token>`)

- **`GET /api/data`**: Listagem paginada de arquivos cadastrados.
- **`GET /api/data/{id}`**: Detalhes do registro `DataFile`.
- **`GET /api/data/{id}/raw`**: Retorna o conteúdo textual bruto (XML/texto puro).
- **`GET /api/data/comicinfo/{comicInfoId}`**: Lista todos os arquivos vinculados ao `ComicInfo`.
- **`GET /api/data/comicinfo/{comicInfoId}/page`**: Lista paginada dos arquivos de um `ComicInfo`.
- **`GET /api/data/opf/{opfId}`**: Lista todos os arquivos vinculados ao `Opf`.
- **`GET /api/data/opf/{opfId}/page`**: Lista paginada dos arquivos de um `Opf`.
- **`POST /api/data`**: Salva um novo arquivo bruto no banco.
- **`PUT /api/data/{id}`**: Atualiza informações do arquivo.
- **`DELETE /api/data/{id}`**: Exclui o arquivo.
