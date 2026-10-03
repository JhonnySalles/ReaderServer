package br.com.fenix.readerserver.controller.data

import br.com.fenix.readerserver.controller.GenericJpaController
import br.com.fenix.readerserver.converters.MediaTypes
import br.com.fenix.readerserver.dto.data.DataFileDto
import br.com.fenix.readerserver.model.data.DataFile
import br.com.fenix.readerserver.service.data.DataFileService
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
@RequestMapping("/api/data")
@Tag(name = "Data", description = "Endpoints para gerenciamento e consulta de arquivos brutos (XML/OPF)")
class DataFileController(
    private val dataFileService: DataFileService
) : GenericJpaController<UUID?, DataFile, DataFileDto, DataFileService>(dataFileService) {

    @Operation(summary = "Busca arquivos por ComicInfo ID", description = "Retorna os arquivos vinculados a um ComicInfo.")
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
    ): ResponseEntity<List<DataFileDto>> {
        return ResponseEntity.ok(dataFileService.findByComicInfoId(comicInfoId))
    }

    @Operation(summary = "Busca paginada de arquivos por ComicInfo ID", description = "Retorna página com os arquivos vinculados a um ComicInfo.")
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
        assembler: PagedResourcesAssembler<DataFileDto>
    ): ResponseEntity<PagedModel<EntityModel<DataFileDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "id"))
        return ResponseEntity.ok(dataFileService.findByComicInfoIdPaged(comicInfoId, pageable, assembler))
    }

    @Operation(summary = "Busca arquivos por OPF ID", description = "Retorna os arquivos vinculados a um OPF.")
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
    ): ResponseEntity<List<DataFileDto>> {
        return ResponseEntity.ok(dataFileService.findByOpfId(opfId))
    }

    @Operation(summary = "Busca paginada de arquivos por OPF ID", description = "Retorna página com os arquivos vinculados a um OPF.")
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
        assembler: PagedResourcesAssembler<DataFileDto>
    ): ResponseEntity<PagedModel<EntityModel<DataFileDto>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, "id"))
        return ResponseEntity.ok(dataFileService.findByOpfIdPaged(opfId, pageable, assembler))
    }

    @Operation(summary = "Obter conteúdo do arquivo bruto (Texto/XML)", description = "Retorna o conteúdo textual bruto do arquivo.")
    @GetMapping(
        value = ["/{id}/raw"],
        produces = [
            MediaType.TEXT_PLAIN_VALUE,
            MediaType.APPLICATION_XML_VALUE
        ]
    )
    fun getRawContent(@PathVariable("id") id: UUID): ResponseEntity<String> {
        val dataFile = dataFileService.findById(id)
        return ResponseEntity.ok(dataFile.fileContent ?: "")
    }
}
