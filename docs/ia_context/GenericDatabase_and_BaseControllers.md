# Acesso a Dados Genéricos e Base Controllers

## 🎯 Objetivo / Contexto
Prover uma infraestrutura DRY (Don't Repeat Yourself) para a API, abstraindo rotinas comuns de CRUD (Create, Read, Update, Delete) em controladores e serviços base. Dessa forma, qualquer nova entidade do sistema pode herdar os comportamentos padrão de listagem, paginação, suporte HATEOAS e persistência.

## 🧩 Arquivos e Componentes
- **Controlador Base:** `br.com.fenix.readerserver.controller.GenericJpaController`
- **Serviço Base:** `br.com.fenix.readerserver.service.GenericJpaService`
- **Contratos (Interfaces/Classes Abstratas):**
  - `EntityBase<ID, E>`: Entidade base que garante a existência de um identificador.
  - `DtoBase<ID>`: DTO base contendo o campo identificador.

## ⚙️ Regras de Negócio e Lógica (Core Logic)
- **Comportamento Genérico (`GenericJpaService`):**
  - Abstrai a injeção do repositório (`JpaRepository`), mapeador (`ModelMapper`/Conversão manual) e classes envolvidas no CRUD.
  - Possui lógicas para `findAll` (paginado), `findById`, `create`, `update` e `delete`.
  - Trata o casting em tempo de execução para os IDs para garantir a compatibilidade de tipos genéricos no Kotlin.
- **Controladores Rest (`GenericJpaController`):**
  - Define as rotas padrão HTTP (GET, POST, PUT, DELETE) aplicadas à entidade especificada na subclasse.
  - Usa suporte `PagedResourcesAssembler` do Spring HATEOAS para injetar links de navegação em retornos de lista (ex. `self`, `next`, `prev`).
- **Negociação de Conteúdo (Content Negotiation):**
  - Responde nos formatos `application/json`, `application/xml`, `application/x-yaml` baseado nos cabeçalhos HTTP aceitos na requisição.

## 🔄 Endpoints Genéricos (Herdados por Controladores de Domínio)
*Assumindo que o mapeamento base seja `/api/recurso/v1`*

- **`GET /api/recurso/v1`**: Busca paginada. Retorna lista HATEOAS de DTOs.
- **`GET /api/recurso/v1/{id}`**: Busca individual. Retorna o DTO correspondente.
- **`POST /api/recurso/v1`**: Insere um novo registro.
- **`PUT /api/recurso/v1`**: Atualiza registro existente (identificador deve ser passado).
- **`DELETE /api/recurso/v1/{id}`**: Exclui o registro informado.
