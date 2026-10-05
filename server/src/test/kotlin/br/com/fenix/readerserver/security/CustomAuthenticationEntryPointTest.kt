package br.com.fenix.readerserver.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import java.io.PrintWriter
import java.io.StringWriter

class CustomAuthenticationEntryPointTest {

    private lateinit var entryPoint: CustomAuthenticationEntryPoint
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse

    @BeforeEach
    fun setUp() {
        entryPoint = CustomAuthenticationEntryPoint()
        request = mockk()
        response = mockk(relaxed = true)
    }

    @Test
    @DisplayName("Deve configurar status 401, MediaType JSON e escrever corpo do erro na resposta")
    fun `deve tratar falha de autenticacao e responder 401 em JSON`() {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)

        every { request.requestURI } returns "/api/book/1"
        every { response.writer } returns printWriter

        val authException = BadCredentialsException("Token ausente ou expirado")

        entryPoint.commence(request, response, authException)

        verify { response.status = HttpServletResponse.SC_UNAUTHORIZED }
        verify { response.contentType = MediaType.APPLICATION_JSON_VALUE }

        val jsonOutput = stringWriter.toString()
        assertNotNull(jsonOutput)
        assertTrue(jsonOutput.contains("\"status\":401"))
        assertTrue(jsonOutput.contains("\"error\":\"Unauthorized\""))
        assertTrue(jsonOutput.contains("\"path\":\"/api/book/1\""))
    }
}
