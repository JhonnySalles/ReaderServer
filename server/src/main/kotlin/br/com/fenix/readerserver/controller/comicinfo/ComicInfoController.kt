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
}
