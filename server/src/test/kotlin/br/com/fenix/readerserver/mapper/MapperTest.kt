package br.com.fenix.readerserver.mapper

import br.com.fenix.readerserver.dto.book.BookDto
import br.com.fenix.readerserver.dto.manga.MangaDto
import br.com.fenix.readerserver.model.book.Book
import br.com.fenix.readerserver.model.manga.Manga
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import org.springframework.data.domain.PageImpl
import java.util.*

class MapperTest {

    private lateinit var mapper: Mapper

    @BeforeEach
    fun setUp() {
        mapper = Mapper(ModelMapper())
    }

    @Test
    @DisplayName("Deve mapear corretamente um Book para BookDto")
    fun `deve mapear Book para BookDto`() {
        val id = UUID.randomUUID()
        val book = Book(
            id = id,
            nome = "Clean Code",
            fileName = "clean_code.epub",
            serie = "Software Engineering",
            volume = 1f
        )

        val dto = mapper.parse(book, BookDto::class.java)

        assertNotNull(dto)
        assertEquals(id, dto.getId())
        assertEquals("Clean Code", dto.nome)
        assertEquals("clean_code.epub", dto.fileName)
        assertEquals("Software Engineering", dto.serie)
        assertEquals(1f, dto.volume)
    }

    @Test
    @DisplayName("Deve mapear corretamente um BookDto para Book")
    fun `deve mapear BookDto para Book`() {
        val id = UUID.randomUUID()
        val dto = BookDto(
            id = id,
            nome = "Refactoring",
            fileName = "refactoring.epub",
            serie = "Software Engineering",
            volume = 2f
        )

        val entity = mapper.parse(dto, Book::class.java)

        assertNotNull(entity)
        assertEquals(id, entity.getId())
        assertEquals("Refactoring", entity.nome)
        assertEquals("refactoring.epub", entity.fileName)
    }

    @Test
    @DisplayName("Deve mapear uma lista de entidades para lista de DTOs")
    fun `deve mapear lista de Manga para lista de MangaDto`() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val mangas = listOf(
            Manga(id = id1, nome = "Naruto", fileName = "naruto_01.cbz"),
            Manga(id = id2, nome = "One Piece", fileName = "one_piece_01.cbz")
        )

        val dtos = mapper.parse(mangas, MangaDto::class.java)

        assertNotNull(dtos)
        assertEquals(2, dtos.size)
        assertEquals("Naruto", dtos[0].nome)
        assertEquals("One Piece", dtos[1].nome)
    }

    @Test
    @DisplayName("Deve mapear uma Page de entidades para Page de DTOs")
    fun `deve mapear Page de entidades para Page de DTOs`() {
        val id = UUID.randomUUID()
        val books = listOf(Book(id = id, nome = "Domain-Driven Design", fileName = "ddd.epub"))
        val page = PageImpl(books)

        val dtoPage = mapper.parse(page, BookDto::class.java)

        assertNotNull(dtoPage)
        assertEquals(1, dtoPage.totalElements)
        assertEquals("Domain-Driven Design", dtoPage.content[0].nome)
    }
}
