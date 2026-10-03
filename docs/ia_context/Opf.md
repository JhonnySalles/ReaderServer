# OPF (Open Packaging Format / ePub Metadata)

## 🎯 Objetivo / Contexto
O componente `Opf` no `ReaderServer` gerencia o arquivamento, consulta e manipulação dos metadados de ePubs baseados na especificação OPF do IDPF / Calibre (Dublin Core metadata: title, creator, contributor, publisher, date, description, subjects, language, identifiers, series, etc.).

## 🧩 Arquivos e Componentes
- **Controlador:** `br.com.fenix.readerserver.controller.opf.OpfController`
- **Serviço:** `br.com.fenix.readerserver.service.opf.OpfService`
- **Repositório:** `br.com.fenix.readerserver.repository.opf.OpfRepository`
- **Modelo/Entidade:** `br.com.fenix.readerserver.model.opf.Opf`
- **DTO:** `br.com.fenix.readerserver.dto.opf.OpfDto`

## ⚙️ Regras de Negócio e Lógica (Core Logic)
- **Mapeamento Genérico:** O `OpfController` e `OpfService` herdam de `GenericJpaController` e `GenericJpaService`, provendo operações completas de CRUD (paginação, busca por ID, inserção, atualização e exclusão).
- **Consultas Personalizadas:** Suporte a busca paginada por `title`, `creator` e `series`.
- **Formato XML/JAXB:** Mapeado com `@XmlRootElement(name = "package", namespace = "http://www.idpf.org/2007/opf")` para serialização/deserialização direta.
- **Auditoria no Banco:** O campo `atualizacao` é gerenciado exclusivamente no banco de dados com `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`, sem ser mapeado no modelo JPA.

## 🔄 Endpoints Principais
Base: `/api/opf` (requer autenticação JWT via `Authorization: Bearer <token>`)

- **`GET /api/opf`**: Retorna listagem paginada de OPFs com suporte a HATEOAS.
- **`GET /api/opf/{id}`**: Busca um OPF por UUID.
- **`POST /api/opf`**: Cria um novo registro de metadados OPF.
- **`PUT /api/opf/{id}`**: Atualiza os dados de um OPF.
- **`DELETE /api/opf/{id}`**: Exclui o OPF.
- **`GET /api/opf/search/title?title={title}`**: Busca paginada por título.
- **`GET /api/opf/search/creator?creator={creator}`**: Busca paginada por autor/criador.
- **`GET /api/opf/search/series?series={series}`**: Busca paginada por série.
