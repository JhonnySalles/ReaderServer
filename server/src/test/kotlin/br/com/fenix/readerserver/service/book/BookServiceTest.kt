package br.com.fenix.readerserver.service.book

import br.com.fenix.readerserver.dto.book.BookDto
import br.com.fenix.readerserver.exceptions.InvalidNotFoundException
import br.com.fenix.readerserver.exceptions.RequiredObjectIsNullException
import br.com.fenix.readerserver.model.book.Book
import br.com.fenix.readerserver.repository.book.BookRepository
import br.com.fenix.readerserver.repository.data.DataFileRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.modelmapper.ModelMapper
import java.util.*

class BookServiceTest {

    private lateinit var bookRepository: BookRepository
    private lateinit var dataFileRepository: DataFileRepository
    private lateinit var modelMapper: ModelMapper
    private lateinit var bookService: BookService

    @BeforeEach
    fun setUp() {
        bookRepository = mockk()
        dataFileRepository = mockk()
        modelMapper = ModelMapper()
        bookService = BookService(bookRepository, dataFileRepository, modelMapper)
    }

    @Test
    @DisplayName("Deve lançar RequiredObjectIsNullException quando o ID informado for nulo")
    fun `deve lancar excecao quando id for nulo`() {
        assertThrows<RequiredObjectIsNullException> {
            bookService.findById(null)
        }
    }

    @Test
    @DisplayName("Deve lançar InvalidNotFoundException quando o livro não for encontrado pelo ID")
    fun `deve lancar excecao quando livro nao for encontrado`() {
        val id = UUID.randomUUID()
        every { bookRepository.findByIdWithOpf(id) } returns Optional.empty()

        assertThrows<InvalidNotFoundException> {
            bookService.findById(id)
        }

        verify(exactly = 1) { bookRepository.findByIdWithOpf(id) }
    }

    @Test
    @DisplayName("Deve retornar BookDto com sucesso quando o livro for encontrado pelo ID")
    fun `deve retornar BookDto quando encontrado`() {
        val id = UUID.randomUUID()
        val book = Book(
            id = id,
            nome = "Clean Architecture",
            fileName = "clean_arch.epub"
        )

        every { bookRepository.findByIdWithOpf(id) } returns Optional.of(book)

        val result = bookService.findById(id)

        assertNotNull(result)
        assertEquals("Clean Architecture", result.nome)
        assertEquals("clean_arch.epub", result.fileName)
        verify(exactly = 1) { bookRepository.findByIdWithOpf(id) }
    }
}
