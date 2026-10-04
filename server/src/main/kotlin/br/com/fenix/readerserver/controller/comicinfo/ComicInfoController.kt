package br.com.fenix.readerserver.controller.comicinfo

import br.com.fenix.readerserver.converters.MediaTypes
import br.com.fenix.readerserver.controller.GenericJpaController
import br.com.fenix.readerserver.dto.comicinfo.ComicInfoDto
import br.com.fenix.readerserver.model.comicinfo.ComicInfo
import br.com.fenix.readerserver.service.comicinfo.ComicInfoService
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
@RequestMapping("/api/comicinfo")
@Tag(name = "Comic Info", description = "Endpoints para gerenciamento e comunicação de ComicInfo")
class ComicInfoController(
    private val comicInfoService: ComicInfoService
) : GenericJpaController<UUID?, ComicInfo, ComicInfoDto, ComicInfoService>(comicInfoService) {

    @Operation(summary = "Busca por Série", description = "Busca paginada de ComicInfo pelo nome ou trecho da série.")
    @GetMapping(
        value = ["/search/series"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findBySeries(
        @RequestParam("series") series: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<ComicInfoDto>
    ): ResponseEntity<PagedModel<EntityModel<ComicInfoDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "series"))
        return ResponseEntity.ok(comicInfoService.findBySeries(series, pageable, assembler))
    }

    @Operation(summary = "Busca por Título", description = "Busca paginada de ComicInfo pelo título.")
    @GetMapping(
        value = ["/search/title"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByTitle(
        @RequestParam("title") title: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<ComicInfoDto>
    ): ResponseEntity<PagedModel<EntityModel<ComicInfoDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "title"))
        return ResponseEntity.ok(comicInfoService.findByTitle(title, pageable, assembler))
    }

    @Operation(summary = "Busca por Comic", description = "Retorna lista de ComicInfo pelo campo comic.")
    @GetMapping(
        value = ["/search/comic"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByComic(
        @RequestParam("comic") comic: String
    ): ResponseEntity<List<ComicInfoDto>> {
        return ResponseEntity.ok(comicInfoService.findByComic(comic))
    }

    @Operation(summary = "Busca por Série, Volume e Linguagem", description = "Retorna lista de ComicInfo pela combinação de Série, Volume e Linguagem.")
    @GetMapping(
        value = ["/search/serie-volume-language"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findBySeriesAndVolumeAndLanguage(
        @RequestParam("serie") serie: String,
        @RequestParam("volume") volume: Float,
        @RequestParam("language") language: String
    ): ResponseEntity<List<ComicInfoDto>> {
        return ResponseEntity.ok(comicInfoService.findBySeriesAndVolumeAndLanguage(serie, volume, language))
    }

    @Operation(summary = "Busca por Título, Volume e Linguagem", description = "Retorna lista de ComicInfo pela combinação de Título, Volume e Linguagem.")
    @GetMapping(
        value = ["/search/title-volume-language"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByTitleAndVolumeAndLanguage(
        @RequestParam("title") title: String,
        @RequestParam("volume") volume: Float,
        @RequestParam("language") language: String
    ): ResponseEntity<List<ComicInfoDto>> {
        return ResponseEntity.ok(comicInfoService.findByTitleAndVolumeAndLanguage(title, volume, language))
    }

    @Operation(summary = "Busca Avançada de ComicInfo (GET)", description = "Busca paginada com parâmetros na query.")
    @GetMapping(
        value = ["/search/advanced"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun searchAdvancedGet(
        @ModelAttribute filter: br.com.fenix.readerserver.dto.comicinfo.ComicInfoSearchFilterDto,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<ComicInfoDto>
    ): ResponseEntity<PagedModel<EntityModel<ComicInfoDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "title"))
        return ResponseEntity.ok(comicInfoService.searchAdvanced(filter, pageable, assembler))
    }

    @Operation(summary = "Busca Avançada de ComicInfo (POST)", description = "Busca paginada com múltiplos filtros no body.")
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
        @RequestBody filter: br.com.fenix.readerserver.dto.comicinfo.ComicInfoSearchFilterDto,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<ComicInfoDto>
    ): ResponseEntity<PagedModel<EntityModel<ComicInfoDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "title"))
        return ResponseEntity.ok(comicInfoService.searchAdvanced(filter, pageable, assembler))
    }
    @Operation(summary = "Download ComicInfo XML", description = "Faz o download do arquivo ComicInfo.xml.")
    @GetMapping(
        value = ["/{id}/download"],
        produces = [
            MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_XML_VALUE,
            MediaType.TEXT_PLAIN_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            MediaType.ALL_VALUE
        ]
    )
    fun downloadXml(@PathVariable("id") id: UUID): ResponseEntity<String> {
        val dto = comicInfoService.findById(id)
        val dataFiles = comicInfoService.findRawDataFiles(id)
        val content = if (dataFiles.isNotEmpty() && !dataFiles[0].fileContent.isNullOrBlank()) {
            dataFiles[0].fileContent!!
        } else {
            // Se não houver arquivo bruto persistido, gera o XML básico
            """<?xml version="1.0" encoding="utf-8"?>
<ComicInfo xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
  <Title>${dto.title}</Title>
  <Series>${dto.series}</Series>
  <Number>${dto.number}</Number>
  <Volume>${dto.volume}</Volume>
  <Writer>${dto.writer ?: ""}</Writer>
  <Publisher>${dto.publisher ?: ""}</Publisher>
  <Genre>${dto.genre ?: ""}</Genre>
  <LanguageISO>${dto.languageISO}</LanguageISO>
  <Summary>${dto.summary ?: ""}</Summary>
</ComicInfo>"""
        }
        val cleanName = (dto.series.ifEmpty { dto.title }.ifEmpty { "ComicInfo" })
            .replace("[^a-zA-Z0-9\\-_\\.]".toRegex(), "_")
        val filename = if (cleanName.lowercase().endsWith(".xml")) cleanName else "$cleanName.xml"

        return ResponseEntity.ok()
            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .contentType(MediaType.parseMediaType("application/xml;charset=UTF-8"))
            .body(content)
    }
}
