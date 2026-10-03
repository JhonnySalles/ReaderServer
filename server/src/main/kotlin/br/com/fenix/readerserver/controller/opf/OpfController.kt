package br.com.fenix.readerserver.controller.opf

import br.com.fenix.readerserver.controller.GenericJpaController
import br.com.fenix.readerserver.converters.MediaTypes
import br.com.fenix.readerserver.dto.opf.OpfDto
import br.com.fenix.readerserver.model.opf.Opf
import br.com.fenix.readerserver.service.opf.OpfService
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
@RequestMapping("/api/opf")
@Tag(name = "OPF", description = "Endpoints para gerenciamento e comunicação de OPF (Metadados de ePub)")
class OpfController(
    private val opfService: OpfService
) : GenericJpaController<UUID?, Opf, OpfDto, OpfService>(opfService) {

    @Operation(summary = "Busca por Título", description = "Busca paginada de OPF pelo título.")
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
        assembler: PagedResourcesAssembler<OpfDto>
    ): ResponseEntity<PagedModel<EntityModel<OpfDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "title"))
        return ResponseEntity.ok(opfService.findByTitle(title, pageable, assembler))
    }

    @Operation(summary = "Busca por Autor/Criador", description = "Busca paginada de OPF pelo autor/criador.")
    @GetMapping(
        value = ["/search/creator"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByCreator(
        @RequestParam("creator") creator: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<OpfDto>
    ): ResponseEntity<PagedModel<EntityModel<OpfDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "creator"))
        return ResponseEntity.ok(opfService.findByCreator(creator, pageable, assembler))
    }

    @Operation(summary = "Busca por Série", description = "Busca paginada de OPF pelo nome da série.")
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
        assembler: PagedResourcesAssembler<OpfDto>
    ): ResponseEntity<PagedModel<EntityModel<OpfDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "series"))
        return ResponseEntity.ok(opfService.findBySeries(series, pageable, assembler))
    }

    @Operation(summary = "Busca Avançada de OPF (GET)", description = "Busca paginada com parâmetros na query.")
    @GetMapping(
        value = ["/search/advanced"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun searchAdvancedGet(
        @ModelAttribute filter: br.com.fenix.readerserver.dto.opf.OpfSearchFilterDto,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<OpfDto>
    ): ResponseEntity<PagedModel<EntityModel<OpfDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "title"))
        return ResponseEntity.ok(opfService.searchAdvanced(filter, pageable, assembler))
    }

    @Operation(summary = "Busca Avançada de OPF (POST)", description = "Busca paginada com múltiplos filtros no body.")
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
        @RequestBody filter: br.com.fenix.readerserver.dto.opf.OpfSearchFilterDto,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        assembler: PagedResourcesAssembler<OpfDto>
    ): ResponseEntity<PagedModel<EntityModel<OpfDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "title"))
        return ResponseEntity.ok(opfService.searchAdvanced(filter, pageable, assembler))
    }

    @Operation(summary = "Download OPF XML", description = "Faz o download do arquivo .opf.")
    @GetMapping(
        value = ["/{id}/download"],
        produces = [MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_PLAIN_VALUE]
    )
    fun downloadXml(@PathVariable("id") id: UUID): ResponseEntity<String> {
        val dto = opfService.findById(id)
        val dataFiles = opfService.findRawDataFiles(id)
        val content = if (dataFiles.isNotEmpty() && !dataFiles[0].fileContent.isNullOrBlank()) {
            dataFiles[0].fileContent!!
        } else {
            """<?xml version="1.0" encoding="utf-8"?>
<package xmlns="http://www.idpf.org/2007/opf" version="3.0">
  <metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
    <dc:title>${dto.title}</dc:title>
    <dc:creator>${dto.creator ?: ""}</dc:creator>
    <dc:publisher>${dto.publisher ?: ""}</dc:publisher>
    <dc:language>${dto.language}</dc:language>
    <dc:date>${dto.datePublished ?: ""}</dc:date>
    <dc:description>${dto.description ?: ""}</dc:description>
    <dc:subject>${dto.subjects ?: ""}</dc:subject>
  </metadata>
</package>"""
        }
        val cleanName = (dto.title.ifEmpty { "content" })
            .replace("[^a-zA-Z0-9\\-_\\.]".toRegex(), "_")
        val filename = if (cleanName.lowercase().endsWith(".opf")) cleanName else "$cleanName.opf"

        return ResponseEntity.ok()
            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .contentType(MediaType.APPLICATION_XML)
            .body(content)
    }
}
