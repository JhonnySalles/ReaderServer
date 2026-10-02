# ComicInfo

## 🎯 Objetivo / Contexto
O componente `ComicInfo` no `ReaderServer` gerencia o arquivamento e leitura dos metadados de arquivos `.cbz`/`.cbr` utilizados para quadrinhos e mangás. Baseado na especificação oficial do `ComicInfo.xml`, esses dados permitem que os aplicativos consigam catalogar títulos, autores, capítulos e outras informações.

## 🧩 Arquivos e Componentes
- **Controlador:** `br.com.fenix.readerserver.controller.ComicInfoController`
- **Serviço:** `br.com.fenix.readerserver.service.ComicInfoService`
- **Repositório:** `br.com.fenix.readerserver.repository.ComicInfoRepository`
- **Modelo/Entidade:** `br.com.fenix.readerserver.model.ComicInfo`
- **DTO:** `br.com.fenix.readerserver.model.dto.ComicInfoDto`

## ⚙️ Regras de Negócio e Lógica (Core Logic)
- **Mapeamento Genérico:** O `ComicInfoController` e `ComicInfoService` estendem das respectivas classes base (`GenericJpaController` e `GenericJpaService`). Devido a essa herança, toda a lógica de persistência e endpoints REST já está automaticamente implementada.
- **Estrutura de Dados:**
  - A entidade `ComicInfo` foi mapeada tanto para serialização JSON quanto XML (via JAXB `@XmlRootElement`).
  - Representa campos padrão como `Title`, `Series`, `Number`, `Summary`, `Writer`, `Penciller`, `Publisher`, `Genre`, etc.
- **Suporte Multi-Formato:** Ao consultar o endpoint via `Accept: application/xml`, a API retornará os dados perfeitamente no formato que aplicativos de mangás e comics nativamente leem de dentro dos arquivos zip.

## 🔄 Endpoints Baseados no Caminho Genérico
Todos localizados em `/api/comic-info/v1` exigindo autenticação (Bearer Token).

- **`GET /api/comic-info/v1`**: Lista as informações de comics disponíveis na base. Suporta parâmetros genéricos de página.
- **`GET /api/comic-info/v1/{id}`**: Retorna detalhes de um `ComicInfo` específico.
- **`POST /api/comic-info/v1`**: Grava os metadados extraídos de um arquivo para o banco de dados.
- **`PUT /api/comic-info/v1`**: Atualiza dados existentes (ex. correção de título, ou novo volume).
- **`DELETE /api/comic-info/v1/{id}`**: Remove o registro.
