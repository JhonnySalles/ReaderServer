package br.com.fenix.readerserver.repository.manga

import br.com.fenix.readerserver.dto.manga.MangaSearchFilterDto
import br.com.fenix.readerserver.model.comicinfo.ComicInfo
import br.com.fenix.readerserver.model.manga.Manga
import jakarta.persistence.criteria.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MangaSpecificationTest {

    private lateinit var root: Root<Manga>
    private lateinit var query: CriteriaQuery<*>
    private lateinit var cb: CriteriaBuilder
    private lateinit var comicInfoJoin: Join<Manga, ComicInfo>
    private lateinit var predicate: Predicate
    private lateinit var pathString: Path<String>
    private lateinit var pathFloat: Path<Float>

    @BeforeEach
    fun setUp() {
        root = mockk(relaxed = true)
        query = mockk(relaxed = true)
        cb = mockk(relaxed = true)
        comicInfoJoin = mockk(relaxed = true)
        predicate = mockk(relaxed = true)
        pathString = mockk(relaxed = true)
        pathFloat = mockk(relaxed = true)

        every { root.join<Manga, ComicInfo>("comicInfo", JoinType.LEFT) } returns comicInfoJoin
        every { root.get<String>(any<String>()) } returns pathString
        every { root.get<Float>(any<String>()) } returns pathFloat
        every { comicInfoJoin.get<String>(any<String>()) } returns pathString
        every { comicInfoJoin.get<Float>(any<String>()) } returns pathFloat
        every { cb.like(any(), any<String>()) } returns predicate
        every { cb.equal(any(), any()) } returns predicate
        every { cb.or(*anyVararg()) } returns predicate
        every { cb.and(*anyVararg()) } returns predicate
        every { cb.conjunction() } returns predicate
    }

    @Test
    @DisplayName("Deve gerar conjunção vazia quando nenhum filtro de manga for informado")
    fun `deve retornar conjunction quando filtros de manga forem vazios`() {
        val filter = MangaSearchFilterDto()
        val spec = MangaSpecification.withFilters(filter)

        val result = spec.toPredicate(root, query, cb)

        assertNotNull(result)
        verify { cb.conjunction() }
    }

    @Test
    @DisplayName("Deve construir predicado de busca textual ampla para manga")
    fun `deve aplicar filtro query em manga`() {
        val filter = MangaSearchFilterDto(query = "titan")
        val spec = MangaSpecification.withFilters(filter)

        val result = spec.toPredicate(root, query, cb)

        assertNotNull(result)
        verify { cb.or(*anyVararg()) }
        verify { cb.and(*anyVararg()) }
    }

    @Test
    @DisplayName("Deve construir predicados para série, volume e gênero")
    fun `deve aplicar filtros de serie volume e genero em manga`() {
        val filter = MangaSearchFilterDto(serie = "Attack on Titan", volume = "1.0", genre = "Ação")
        val spec = MangaSpecification.withFilters(filter)

        val result = spec.toPredicate(root, query, cb)

        assertNotNull(result)
        verify { cb.and(*anyVararg()) }
    }
}
