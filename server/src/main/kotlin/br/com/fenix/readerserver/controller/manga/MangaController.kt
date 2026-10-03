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
}
