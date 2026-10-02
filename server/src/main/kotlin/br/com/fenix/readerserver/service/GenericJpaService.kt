package br.com.fenix.readerserver.service

import br.com.fenix.readerserver.dto.DtoBase
import br.com.fenix.readerserver.exceptions.InvalidNotFoundException
import br.com.fenix.readerserver.exceptions.RequiredObjectIsNullException
import br.com.fenix.readerserver.mapper.Mapper
import br.com.fenix.readerserver.model.EntityBase
import br.com.fenix.readerserver.model.EntityFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.transaction.annotation.Transactional

abstract class GenericJpaService<ID, E : EntityBase<ID, E>, D : DtoBase<ID>>(
    val factory: EntityFactory<ID, E>,
    val clazzEntity: Class<E>,
    val clazzDto: Class<D>
) {

    abstract val repository: JpaRepository<E, in ID>
    abstract val mapper: Mapper

    @Suppress("UNCHECKED_CAST")
    @Transactional(readOnly = true)
    open fun findById(id: ID): D {
        if (id == null) throw RequiredObjectIsNullException("ID cannot be null")
        val entity = (repository as JpaRepository<E, Any>).findById(id as Any).orElseThrow {
            InvalidNotFoundException("No records found for ID: $id")
        }
        return mapper.parse(entity, clazzDto)
    }

    @Transactional(readOnly = true)
    open fun findAll(): List<D> {
        val list = repository.findAll()
        return mapper.parse(list, clazzDto)
    }

    @Transactional(readOnly = true)
    open fun getPage(pageable: Pageable, assembler: PagedResourcesAssembler<D>): PagedModel<EntityModel<D>> {
        val page: Page<E> = repository.findAll(pageable)
        val dtoPage: Page<D> = page.map { mapper.parse(it, clazzDto) }
        return assembler.toModel(dtoPage)
    }

    @Transactional
    open fun create(dto: D?): D {
        if (dto == null) throw RequiredObjectIsNullException()
        val entity = mapper.parse(dto, clazzEntity)
        val saved = repository.save(entity)
        return mapper.parse(saved, clazzDto)
    }

    @Suppress("UNCHECKED_CAST")
    @Transactional
    open fun update(id: ID, dto: D?): D {
        if (id == null || dto == null) throw RequiredObjectIsNullException()
        val existing = (repository as JpaRepository<E, Any>).findById(id as Any).orElseThrow {
            InvalidNotFoundException("No records found for ID: $id")
        }
        val source = mapper.parse(dto, clazzEntity)
        existing.merge(source)
        val updated = repository.save(existing)
        return mapper.parse(updated, clazzDto)
    }

    @Suppress("UNCHECKED_CAST")
    @Transactional
    open fun patch(id: ID, dto: D?): D {
        if (id == null || dto == null) throw RequiredObjectIsNullException()
        val existing = (repository as JpaRepository<E, Any>).findById(id as Any).orElseThrow {
            InvalidNotFoundException("No records found for ID: $id")
        }
        val source = mapper.parse(dto, clazzEntity)
        existing.patch(source)
        val updated = repository.save(existing)
        return mapper.parse(updated, clazzDto)
    }

    @Suppress("UNCHECKED_CAST")
    @Transactional
    open fun delete(id: ID) {
        if (id == null) throw RequiredObjectIsNullException("ID cannot be null")
        val existing = (repository as JpaRepository<E, Any>).findById(id as Any).orElseThrow {
            InvalidNotFoundException("No records found for ID: $id")
        }
        repository.delete(existing)
    }
}
