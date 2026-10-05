package br.com.fenix.readerserver.repository.book

import br.com.fenix.readerserver.dto.book.BookSearchFilterDto
import br.com.fenix.readerserver.model.book.Book
import br.com.fenix.readerserver.model.opf.Opf
import jakarta.persistence.criteria.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BookSpecificationTest {

    private lateinit var root: Root<Book>
    private lateinit var query: CriteriaQuery<*>
    private lateinit var cb: CriteriaBuilder
    private lateinit var opfJoin: Join<Book, Opf>
    private lateinit var predicate: Predicate
    private lateinit var pathString: Path<String>
    private lateinit var pathFloat: Path<Float>

    @BeforeEach
    fun setUp() {
        root = mockk(relaxed = true)
        query = mockk(relaxed = true)
        cb = mockk(relaxed = true)
        opfJoin = mockk(relaxed = true)
        predicate = mockk(relaxed = true)
        pathString = mockk(relaxed = true)
        pathFloat = mockk(relaxed = true)

        every { root.join<Book, Opf>("opf", JoinType.LEFT) } returns opfJoin
        every { root.get<String>(any<String>()) } returns pathString
        every { root.get<Float>(any<String>()) } returns pathFloat
        every { opfJoin.get<String>(any<String>()) } returns pathString
        every { opfJoin.get<Float>(any<String>()) } returns pathFloat
        every { cb.like(any(), any<String>()) } returns predicate
        every { cb.equal(any(), any()) } returns predicate
        every { cb.or(*anyVararg()) } returns predicate
        every { cb.and(*anyVararg()) } returns predicate
        every { cb.conjunction() } returns predicate
    }

    @Test
    @DisplayName("Deve gerar conjunção vazia quando nenhum filtro for informado")
    fun `deve retornar conjunction quando filtros forem vazios`() {
        val filter = BookSearchFilterDto()
        val spec = BookSpecification.withFilters(filter)

        val result = spec.toPredicate(root, query, cb)

        assertNotNull(result)
        verify { cb.conjunction() }
    }

    @Test
    @DisplayName("Deve construir predicado de busca textual quando query for preenchida")
    fun `deve aplicar filtro query`() {
        val filter = BookSearchFilterDto(query = "arquitetura")
        val spec = BookSpecification.withFilters(filter)

        val result = spec.toPredicate(root, query, cb)

        assertNotNull(result)
        verify { cb.or(*anyVararg()) }
        verify { cb.and(*anyVararg()) }
    }

    @Test
    @DisplayName("Deve construir predicados para série, volume e autor")
    fun `deve aplicar filtros de serie volume e autor`() {
        val filter = BookSearchFilterDto(serie = "Design Patterns", volume = "1.0", author = "GoF")
        val spec = BookSpecification.withFilters(filter)

        val result = spec.toPredicate(root, query, cb)

        assertNotNull(result)
        verify { cb.and(*anyVararg()) }
    }
}
