package br.com.fenix.readerserver.exceptions

import com.auth0.jwt.exceptions.TokenExpiredException
import jakarta.servlet.http.HttpServletRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException

class CustomizedResponseEntityExceptionHandlerTest {

    private lateinit var handler: CustomizedResponseEntityExceptionHandler
    private lateinit var request: HttpServletRequest

    @BeforeEach
    fun setUp() {
        handler = CustomizedResponseEntityExceptionHandler()
        request = mockk()
        every { request.requestURI } returns "/api/test"
    }

    @Test
    @DisplayName("Deve retornar 404 NOT_FOUND com ExceptionResponse para InvalidNotFoundException")
    fun `deve tratar InvalidNotFoundException`() {
        val ex = InvalidNotFoundException("Livro não encontrado")

        val response = handler.handleNotFoundExceptions(ex, request)

        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        val body = response.body
        assertNotNull(body)
        assertEquals(404, body?.status)
        assertEquals("Not Found", body?.error)
        assertEquals("Livro não encontrado", body?.message)
        assertEquals("/api/test", body?.path)
    }

    @Test
    @DisplayName("Deve retornar 400 BAD_REQUEST para RequiredObjectIsNullException")
    fun `deve tratar RequiredObjectIsNullException`() {
        val ex = RequiredObjectIsNullException("O campo ID é obrigatório")

        val response = handler.handleBadRequestExceptions(ex, request)

        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val body = response.body
        assertNotNull(body)
        assertEquals(400, body?.status)
        assertEquals("Bad Request", body?.error)
        assertEquals("O campo ID é obrigatório", body?.message)
    }

    @Test
    @DisplayName("Deve retornar 403 FORBIDDEN para BadCredentialsException")
    fun `deve tratar BadCredentialsException`() {
        val ex = BadCredentialsException("Usuário ou senha inválidos")

        val response = handler.handleBadCredentialsException(ex, request)

        assertNotNull(response)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        val body = response.body
        assertNotNull(body)
        assertEquals(403, body?.status)
        assertEquals("Usuário ou senha inválidos", body?.message)
    }

    @Test
    @DisplayName("Deve retornar 401 UNAUTHORIZED para TokenExpiredException")
    fun `deve tratar TokenExpiredException`() {
        val ex = TokenExpiredException("The Token has expired on...")

        val response = handler.handleTokenExpiredException(ex, request)

        assertNotNull(response)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        val body = response.body
        assertNotNull(body)
        assertEquals(401, body?.status)
        assertEquals("Token de autenticação expirado.", body?.message)
    }

    @Test
    @DisplayName("Deve retornar 500 INTERNAL_SERVER_ERROR para exceções genéricas")
    fun `deve tratar Exception generica`() {
        val ex = RuntimeException("Erro inesperado no servidor")

        val response = handler.handleAllExceptions(ex, request)

        assertNotNull(response)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        val body = response.body
        assertNotNull(body)
        assertEquals(500, body?.status)
        assertEquals("/api/test", body?.path)
    }
}
