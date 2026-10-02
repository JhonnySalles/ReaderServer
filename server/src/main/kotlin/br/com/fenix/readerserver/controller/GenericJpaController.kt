package br.com.fenix.readerserver.controller

import br.com.fenix.readerserver.converters.MediaTypes
import br.com.fenix.readerserver.dto.DtoBase
import br.com.fenix.readerserver.model.EntityBase
import br.com.fenix.readerserver.service.GenericJpaService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

abstract class GenericJpaController<ID, E : EntityBase<ID, E>, D : DtoBase<ID>, S : GenericJpaService<ID, E, D>>(
    protected val service: S
) {

    @Operation(summary = "Busca paginada", description = "Retorna uma lista paginada dos registros com links de navegação.")
    @GetMapping(
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun getPage(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "20") size: Int,
        @RequestParam(value = "direction", defaultValue = "asc") direction: String,
        @RequestParam(value = "sortField", defaultValue = "id") sortField: String,
        assembler: PagedResourcesAssembler<D>
    ): ResponseEntity<PagedModel<EntityModel<D>>> {
        val sort = if ("desc".equals(direction, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sort, sortField))
        return ResponseEntity.ok(service.getPage(pageable, assembler))
    }

    @Operation(summary = "Busca por ID", description = "Recupera um único registro através do seu identificador único.")
    @GetMapping(
        value = ["/{id}"],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun getById(@PathVariable("id") id: ID): ResponseEntity<D> {
        return ResponseEntity.ok(service.findById(id))
    }

    @Operation(summary = "Criar novo registro", description = "Persiste um novo registro no banco de dados relacional.")
    @PostMapping(
        consumes = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun create(@RequestBody dto: D): ResponseEntity<D> {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))
    }

    @Operation(summary = "Atualizar registro", description = "Atualiza completamente as informações do registro informado.")
    @PutMapping(
        value = ["/{id}"],
        consumes = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun update(@PathVariable("id") id: ID, @RequestBody dto: D): ResponseEntity<D> {
        return ResponseEntity.ok(service.update(id, dto))
    }

    @Operation(summary = "Atualizar parcialmente", description = "Atualiza os campos informados do registro (PATCH).")
    @PatchMapping(
        value = ["/{id}"],
        consumes = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ],
        produces = [
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaTypes.MEDIA_TYPE_APPLICATION_YML_VALUE
        ]
    )
    fun patch(@PathVariable("id") id: ID, @RequestBody dto: D): ResponseEntity<D> {
        return ResponseEntity.ok(service.patch(id, dto))
    }

    @Operation(summary = "Remover registro", description = "Remove o registro informado do banco de dados relacional.")
    @DeleteMapping(value = ["/{id}"])
    fun delete(@PathVariable("id") id: ID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
