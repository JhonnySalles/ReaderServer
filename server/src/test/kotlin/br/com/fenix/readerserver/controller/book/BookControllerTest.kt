package br.com.fenix.readerserver.controller.book

import br.com.fenix.readerserver.dto.book.BookDto
import br.com.fenix.readerserver.dto.data.DataFileDto
import br.com.fenix.readerserver.dto.opf.OpfDto
import br.com.fenix.readerserver.service.book.BookService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class BookControllerTest {

    private lateinit var bookService: BookService
    private lateinit var bookController: BookController

    @BeforeEach
    fun setUp() {
        bookService = mockk()
        bookController = BookController(bookService)
    }

    @Test
    @DisplayName("Deve buscar um único livro por nome de arquivo exato")
    fun `deve buscar livro por nome de arquivo exato`() {
        val fileName = "livro_teste.epub"
        val expectedDto = BookDto(
            id = UUID.randomUUID(),
            nome = "Livro Teste",
            fileName = fileName
        )

        every { bookService.findSingleByFileName(fileName) } returns expectedDto

        val response = bookController.findSingleByFileName(fileName)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedDto, response.body)
        verify(exactly = 1) { bookService.findSingleByFileName(fileName) }
    }

    @Test
    @DisplayName("Deve buscar livros por série e volume")
    fun `deve buscar livros por serie e volume`() {
        val serie = "Algoritmos"
        val volume = 1f
        val expectedList = listOf(
            BookDto(id = UUID.randomUUID(), nome = "Algoritmos Vol 1", serie = serie, volume = volume)
        )

        every { bookService.findBySerieAndVolume(serie, volume) } returns expectedList

        val response = bookController.findBySerieAndVolume(serie, volume)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.size)
        assertEquals(serie, response.body?.get(0)?.serie)
        verify(exactly = 1) { bookService.findBySerieAndVolume(serie, volume) }
    }

    @Test
    @DisplayName("Deve buscar livros vinculados a um OPF ID")
    fun `deve buscar livros por opfId`() {
        val opfId = UUID.randomUUID()
        val expectedList = listOf(
            BookDto(id = UUID.randomUUID(), nome = "Livro com OPF", opfId = opfId)
        )

        every { bookService.findByOpfId(opfId) } returns expectedList

        val response = bookController.findByOpfId(opfId)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.size)
        assertEquals(opfId, response.body?.get(0)?.opfId)
        verify(exactly = 1) { bookService.findByOpfId(opfId) }
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found no download quando o livro não possui opfId")
    fun `deve retornar 404 no download se opfId for nulo`() {
        val id = UUID.randomUUID()
        val bookDto = BookDto(id = id, nome = "Sem OPF", opfId = null)

        every { bookService.findById(id) } returns bookDto

        val response = bookController.downloadLinkedContent(id)

        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    @DisplayName("Deve retornar arquivo OPF XML com cabeçalho Content-Disposition no download")
    fun `deve fazer download do conteudo OPF vinculado com sucesso`() {
        val id = UUID.randomUUID()
        val opfId = UUID.randomUUID()
        val opfDto = OpfDto(id = opfId, title = "Clean Code", creator = "Uncle Bob")
        val bookDto = BookDto(id = id, nome = "Clean Code", opfId = opfId, opf = opfDto)
        val dataFile = DataFileDto(id = UUID.randomUUID(), fileName = "content.opf", fileContent = "<package>test</package>")

        every { bookService.findById(id) } returns bookDto
        every { bookService.findRawDataFilesByOpf(opfId) } returns listOf(dataFile)

        val response = bookController.downloadLinkedContent(id)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("<package>test</package>", response.body)
        assertTrue(response.headers.getFirst("Content-Disposition")?.contains("Clean_Code.opf") == true)
    }
}
