package br.com.fenix.readerserver.controller.book

import br.com.fenix.readerserver.controller.GenericJpaController
import br.com.fenix.readerserver.converters.MediaTypes
import br.com.fenix.readerserver.dto.book.BookDto
import br.com.fenix.readerserver.model.book.Book
import br.com.fenix.readerserver.service.book.BookService
import br.com.fenix.readerserver.util.XmlUtils
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
@RequestMapping("/api/book")
@Tag(name = "Book", description = "Endpoints para gerenciamento e consulta de arquivos físicos de livros/ePubs")
class BookController(
    private val bookService: BookService
) : GenericJpaController<UUID?, Book, BookDto, BookService>(bookService) {

    @Operation(summary = "Busca por Nome", description = "Busca paginada de Book pelo nome de exibição.")
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
        @RequestParam(value = "sortField", defaultValue = "nome") sortField: String,
        assembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<EntityModel<BookDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, sortField))
        return ResponseEntity.ok(bookService.findByNome(nome, pageable, assembler))
    }

    @Operation(summary = "Busca por Nome do Arquivo", description = "Busca paginada de Book pelo nome real do arquivo.")
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
        @RequestParam(value = "sortField", defaultValue = "fileName") sortField: String,
        assembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<EntityModel<BookDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, sortField))
        return ResponseEntity.ok(bookService.findByFileName(fileName, pageable, assembler))
    }

    @Operation(summary = "Busca Exata por Nome do Arquivo", description = "Retorna um único Livro/ePub pelo nome exato do arquivo ou 404 se não encontrado.")
    @GetMapping(
        value = ["/file-name"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findSingleByFileName(
        @RequestParam("fileName") fileName: String
    ): ResponseEntity<BookDto> {
        return ResponseEntity.ok(bookService.findSingleByFileName(fileName))
    }

    @Operation(summary = "Busca por Série e Volume", description = "Retorna uma lista de Livros/ePubs pela série e volume.")
    @GetMapping(
        value = ["/search/serie-volume"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findBySerieAndVolume(
        @RequestParam("serie") serie: String,
        @RequestParam("volume") volume: Float
    ): ResponseEntity<List<BookDto>> {
        return ResponseEntity.ok(bookService.findBySerieAndVolume(serie, volume))
    }

    @Operation(summary = "Busca Paginada de Livros por Série", description = "Retorna página com livros associados à série informada, ordenados por padrão pelo volume.")
    @GetMapping(
        value = ["/search/serie"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findBySerie(
        @RequestParam("serie") serie: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        @RequestParam(value = "sortField", defaultValue = "volume") sortField: String,
        assembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<EntityModel<BookDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, sortField))
        return ResponseEntity.ok(bookService.findBySerie(serie, pageable, assembler))
    }

    @Operation(summary = "Busca Livros por OPF ID", description = "Retorna lista de livros associados a um OPF.")
    @GetMapping(
        value = ["/opf/{opfId}"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByOpfId(
        @PathVariable("opfId") opfId: UUID
    ): ResponseEntity<List<BookDto>> {
        return ResponseEntity.ok(bookService.findByOpfId(opfId))
    }

    @Operation(summary = "Busca paginada de Livros por OPF ID", description = "Retorna página com livros associados a um OPF.")
    @GetMapping(
        value = ["/opf/{opfId}/page"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun findByOpfIdPaged(
        @PathVariable("opfId") opfId: UUID,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        @RequestParam(value = "sortField", defaultValue = "volume") sortField: String,
        assembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<EntityModel<BookDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, sortField))
        return ResponseEntity.ok(bookService.findByOpfIdPaged(opfId, pageable, assembler))
    }

    @Operation(summary = "Busca Avançada de Livros (GET)", description = "Busca paginada com parâmetros na query.")
    @GetMapping(
        value = ["/search/advanced"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun searchAdvancedGet(
        @ModelAttribute filter: br.com.fenix.readerserver.dto.book.BookSearchFilterDto,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        @RequestParam(value = "sortField", defaultValue = "nome") sortField: String,
        assembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<EntityModel<BookDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, sortField))
        return ResponseEntity.ok(bookService.searchAdvanced(filter, pageable, assembler))
    }

    @Operation(summary = "Busca Avançada de Livros (POST)", description = "Busca paginada com múltiplos filtros no body.")
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
        @RequestBody filter: br.com.fenix.readerserver.dto.book.BookSearchFilterDto,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        @RequestParam(value = "sortField", defaultValue = "nome") sortField: String,
        assembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<EntityModel<BookDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, sortField))
        return ResponseEntity.ok(bookService.searchAdvanced(filter, pageable, assembler))
    }

    @Operation(summary = "Download Conteúdo Vinculado do Livro (OPF XML)", description = "Faz o download do OPF associado ao livro.")
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
    fun downloadLinkedContent(@PathVariable("id") id: UUID): ResponseEntity<String> {
        val bookDto = bookService.findById(id)
        val opfId = bookDto.opfId
            ?: return ResponseEntity.notFound().build()

        val dataFiles = bookService.findRawDataFilesByOpf(opfId)
        val content = if (dataFiles.isNotEmpty() && !dataFiles[0].fileContent.isNullOrBlank()) {
            dataFiles[0].fileContent!!
        } else {
            val opf = bookDto.opf
            val title = opf?.title ?: bookDto.nome ?: ""
            // Se não houver arquivo bruto persistido, gera o XML básico com escape seguro
            """<?xml version="1.0" encoding="utf-8"?>
<package xmlns="http://www.idpf.org/2007/opf" version="3.0">
  <metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
    <dc:title>${XmlUtils.escapeXml(title)}</dc:title>
    <dc:creator>${XmlUtils.escapeXml(opf?.creator)}</dc:creator>
    <dc:publisher>${XmlUtils.escapeXml(opf?.publisher)}</dc:publisher>
    <dc:language>${XmlUtils.escapeXml(opf?.language ?: "pt")}</dc:language>
    <dc:date>${XmlUtils.escapeXml(opf?.datePublished)}</dc:date>
    <dc:description>${XmlUtils.escapeXml(opf?.description)}</dc:description>
    <dc:subject>${XmlUtils.escapeXml(opf?.subjects)}</dc:subject>
  </metadata>
</package>"""
        }
        val cleanName = (bookDto.nome ?: bookDto.fileName ?: "content")
            .replace("[^a-zA-Z0-9\\-_\\.]".toRegex(), "_")
        val filename = if (cleanName.lowercase().endsWith(".opf")) cleanName else "$cleanName.opf"

        return ResponseEntity.ok()
            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .contentType(MediaType.parseMediaType("application/xml;charset=UTF-8"))
            .body(content)
    }
}
