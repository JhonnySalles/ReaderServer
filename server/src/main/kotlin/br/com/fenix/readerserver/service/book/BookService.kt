package br.com.fenix.readerserver.service.book

import br.com.fenix.readerserver.dto.book.BookDto
import br.com.fenix.readerserver.exceptions.InvalidNotFoundException
import br.com.fenix.readerserver.exceptions.RequiredObjectIsNullException
import br.com.fenix.readerserver.mapper.Mapper
import br.com.fenix.readerserver.model.book.Book
import br.com.fenix.readerserver.repository.book.BookRepository
import br.com.fenix.readerserver.service.GenericJpaService
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

import br.com.fenix.readerserver.repository.data.DataFileRepository

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val dataFileRepository: DataFileRepository,
    private val modelMapper: ModelMapper
) : GenericJpaService<UUID?, Book, BookDto>(
    Book.Companion,
    Book::class.java,
    BookDto::class.java
) {

    @Suppress("UNCHECKED_CAST")
    override val repository: JpaRepository<Book, in UUID?>
        get() = bookRepository as JpaRepository<Book, in UUID?>

    override val mapper: Mapper
        get() = Mapper(modelMapper)

    @Transactional(readOnly = true)
    override fun findById(id: UUID?): BookDto {
        if (id == null) throw RequiredObjectIsNullException("ID cannot be null")
        val entity = bookRepository.findByIdWithOpf(id).orElseThrow {
            InvalidNotFoundException("No records found for ID: $id")
        }
        return mapper.parse(entity, BookDto::class.java)
    }

    @Transactional(readOnly = true)
    fun findByNome(nome: String, pageable: Pageable, assembler: PagedResourcesAssembler<BookDto>): PagedModel<EntityModel<BookDto>> {
        val page: Page<Book> = bookRepository.findByNome(nome, pageable)
        val dtoPage: Page<BookDto> = page.map { mapper.parse(it, BookDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findByFileName(fileName: String, pageable: Pageable, assembler: PagedResourcesAssembler<BookDto>): PagedModel<EntityModel<BookDto>> {
        val page: Page<Book> = bookRepository.findByFileName(fileName, pageable)
        val dtoPage: Page<BookDto> = page.map { mapper.parse(it, BookDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findByOpfId(opfId: UUID): List<BookDto> {
        return bookRepository.findByOpfId(opfId).map {
            mapper.parse(it, BookDto::class.java)
        }
    }

    @Transactional(readOnly = true)
    fun findByOpfIdPaged(opfId: UUID, pageable: Pageable, assembler: PagedResourcesAssembler<BookDto>): PagedModel<EntityModel<BookDto>> {
        val page: Page<Book> = bookRepository.findByOpfIdPaged(opfId, pageable)
        val dtoPage: Page<BookDto> = page.map { mapper.parse(it, BookDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun searchAdvanced(filter: br.com.fenix.readerserver.dto.book.BookSearchFilterDto, pageable: Pageable, assembler: PagedResourcesAssembler<BookDto>): PagedModel<EntityModel<BookDto>> {
        val spec = br.com.fenix.readerserver.repository.book.BookSpecification.withFilters(filter)
        val page: Page<Book> = bookRepository.findAll(spec, pageable)
        val dtoPage: Page<BookDto> = page.map { mapper.parse(it, BookDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findRawDataFilesByOpf(opfId: UUID): List<br.com.fenix.readerserver.dto.data.DataFileDto> {
        return dataFileRepository.findByOpfId(opfId).map {
            mapper.parse(it, br.com.fenix.readerserver.dto.data.DataFileDto::class.java)
        }
    }
}
