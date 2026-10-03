package br.com.fenix.readerserver.controller.manga

import br.com.fenix.readerserver.controller.GenericJpaController
import br.com.fenix.readerserver.converters.MediaTypes
import br.com.fenix.readerserver.dto.manga.MangaDto
import br.com.fenix.readerserver.model.manga.Manga
import br.com.fenix.readerserver.service.manga.MangaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/manga")
@Tag(name = "Manga", description = "Endpoints para gerenciamento e consulta de arquivos físicos de mangás/quadrinhos")
class MangaController(
    private val mangaService: MangaService
) : GenericJpaController<UUID?, Manga, MangaDto, MangaService>(mangaService) {

    @Operation(summary = "Busca por Nome", description = "Busca paginada de Manga pelo nome de exibição.")
    @GetMapping(
        value = ["/search/nome"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByNome(
        @RequestParam("nome") nome: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<MangaDto>
    ): ResponseEntity<PagedModel<EntityModel<MangaDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "nome"))
        return ResponseEntity.ok(mangaService.findByNome(nome, pageable, assembler))
    }

    @Operation(summary = "Busca por Nome do Arquivo", description = "Busca paginada de Manga pelo nome real do arquivo.")
    @GetMapping(
        value = ["/search/file-name"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByFileName(
        @RequestParam("fileName") fileName: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<MangaDto>
    ): ResponseEntity<PagedModel<EntityModel<MangaDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "fileName"))
        return ResponseEntity.ok(mangaService.findByFileName(fileName, pageable, assembler))
    }

    @Operation(summary = "Busca Mangas por ComicInfo ID", description = "Retorna lista de mangas associados a um ComicInfo.")
    @GetMapping(
        value = ["/comicinfo/{comicInfoId}"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByComicInfoId(
        @PathVariable("comicInfoId") comicInfoId: UUID
    ): ResponseEntity<List<MangaDto>> {
        return ResponseEntity.ok(mangaService.findByComicInfoId(comicInfoId))
    }

    @Operation(summary = "Busca paginada de Mangas por ComicInfo ID", description = "Retorna página com mangas associados a um ComicInfo.")
    @GetMapping(
        value = ["/comicinfo/{comicInfoId}/page"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByComicInfoIdPaged(
        @PathVariable("comicInfoId") comicInfoId: UUID,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<MangaDto>
    ): ResponseEntity<PagedModel<EntityModel<MangaDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "id"))
        return ResponseEntity.ok(mangaService.findByComicInfoIdPaged(comicInfoId, pageable, assembler))
    }

    @Operation(summary = "Busca Avançada de Mangas (GET)", description = "Busca paginada com parâmetros na query.")
    @GetMapping(
        value = ["/search/advanced"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun searchAdvancedGet(
        @ModelAttribute filter: br.com.fenix.readerserver.dto.manga.MangaSearchFilterDto,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<MangaDto>
    ): ResponseEntity<PagedModel<EntityModel<MangaDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "nome"))
        return ResponseEntity.ok(mangaService.searchAdvanced(filter, pageable, assembler))
    }

    @Operation(summary = "Busca Avançada de Mangas (POST)", description = "Busca paginada com múltiplos filtros no body.")
    @PostMapping(
        value = ["/search/advanced"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun searchAdvancedPost(
        @RequestBody filter: br.com.fenix.readerserver.dto.manga.MangaSearchFilterDto,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<MangaDto>
    ): ResponseEntity<PagedModel<EntityModel<MangaDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "nome"))
        return ResponseEntity.ok(mangaService.searchAdvanced(filter, pageable, assembler))
    }

    @Operation(summary = "Download Conteúdo Vinculado do Manga (ComicInfo XML)", description = "Faz o download do ComicInfo.xml associado ao mangá.")
    @GetMapping(
        value = ["/{id}/download"],
        produces = [MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_PLAIN_VALUE]
    )
    fun downloadLinkedContent(@PathVariable("id") id: UUID): ResponseEntity<String> {
        val mangaDto = mangaService.findById(id)
        val comicInfoId = mangaDto.comicInfoId
            ?: return ResponseEntity.notFound().build()

        val dataFiles = mangaService.findRawDataFilesByComicInfo(comicInfoId)
        val content = if (dataFiles.isNotEmpty() && !dataFiles[0].fileContent.isNullOrBlank()) {
            dataFiles[0].fileContent!!
        } else {
            val ci = mangaDto.comicInfo
            val title = ci?.title ?: mangaDto.nome ?: ""
            val series = ci?.series ?: mangaDto.serie ?: ""
            """<?xml version="1.0" encoding="utf-8"?>
<ComicInfo xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
  <Title>$title</Title>
  <Series>$series</Series>
  <Number>${ci?.number ?: 0}</Number>
  <Volume>${ci?.volume ?: 0}</Volume>
  <Writer>${ci?.writer ?: ""}</Writer>
  <Publisher>${ci?.publisher ?: ""}</Publisher>
  <Genre>${ci?.genre ?: ""}</Genre>
  <LanguageISO>${ci?.languageISO ?: "pt"}</LanguageISO>
  <Summary>${ci?.summary ?: ""}</Summary>
</ComicInfo>"""
        }
        val cleanName = (mangaDto.nome ?: mangaDto.fileName ?: "ComicInfo")
            .replace("[^a-zA-Z0-9\\-_\\.]".toRegex(), "_")
        val filename = if (cleanName.lowercase().endsWith(".xml")) cleanName else "$cleanName.xml"

        return ResponseEntity.ok()
            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .contentType(MediaType.APPLICATION_XML)
            .body(content)
    }
}
