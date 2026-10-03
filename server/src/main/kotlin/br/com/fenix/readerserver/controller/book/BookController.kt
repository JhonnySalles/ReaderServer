package br.com.fenix.readerserver.controller.book

import br.com.fenix.readerserver.controller.GenericJpaController
import br.com.fenix.readerserver.converters.MediaTypes
import br.com.fenix.readerserver.dto.book.BookDto
import br.com.fenix.readerserver.model.book.Book
import br.com.fenix.readerserver.service.book.BookService
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
        assembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<EntityModel<BookDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "nome"))
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
        assembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<EntityModel<BookDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "fileName"))
        return ResponseEntity.ok(bookService.findByFileName(fileName, pageable, assembler))
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
        assembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<EntityModel<BookDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "id"))
        return ResponseEntity.ok(bookService.findByOpfIdPaged(opfId, pageable, assembler))
    }
}
