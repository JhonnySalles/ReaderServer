package br.com.fenix.readerserver.controller.auth

import br.com.fenix.readerserver.dto.auth.CredencialDto
import br.com.fenix.readerserver.dto.auth.TokenDto
import br.com.fenix.readerserver.service.auth.AuthService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

class AuthControllerTest {

    private lateinit var authService: AuthService
    private lateinit var authController: AuthController

    @BeforeEach
    fun setUp() {
        authService = mockk()
        authController = AuthController()
        ReflectionTestUtils.setField(authController, "authService", authService)
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar signin com credencial nula ou dados vazios")
    fun `deve retornar 400 no signin quando parametros forem invalidos`() {
        val credencialInvalida = CredencialDto(null, null)

        val response = authController.signin(credencialInvalida)

        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid client request: Username and password are required.", response.body)
    }

    @Test
    @DisplayName("Deve delegar chamada ao AuthService e retornar 200 OK no signin com dados válidos")
    fun `deve autenticar com sucesso no signin`() {
        val credenciais = CredencialDto("jhonny", "123456")
        val tokenDto = TokenDto(
            username = "jhonny",
            authenticated = true,
            created = Date(),
            expiration = Date(),
            accessToken = "jwt_access_token",
            refreshToken = "jwt_refresh_token"
        )

        every { authService.signin(credenciais) } returns ResponseEntity.ok(tokenDto)

        val response = authController.signin(credenciais)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tokenDto, response.body)
        verify(exactly = 1) { authService.signin(credenciais) }
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao renovar token com username ou refresh token nulos/em branco")
    fun `deve retornar 400 no refresh token com parametros nulos`() {
        val response = authController.refreshToken(null, null)

        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response?.statusCode)
        assertEquals("Invalid client request: Username and refresh token are required.", response?.body)
    }

    @Test
    @DisplayName("Deve renovar o token com sucesso e retornar 200 OK")
    fun `deve renovar token com sucesso`() {
        val username = "jhonny"
        val refreshToken = "Bearer valid_refresh_token"
        val expectedToken = TokenDto(
            username = username,
            authenticated = true,
            created = Date(),
            expiration = Date(),
            accessToken = "new_access_token",
            refreshToken = "new_refresh_token"
        )

        every { authService.refreshToken(username, refreshToken) } returns ResponseEntity.ok(expectedToken)

        val response = authController.refreshToken(username, refreshToken)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response?.statusCode)
        assertEquals(expectedToken, response?.body)
        verify(exactly = 1) { authService.refreshToken(username, refreshToken) }
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden se o AuthService retornar null no refresh token")
    fun `deve retornar 403 quando authService retornar null no refresh`() {
        val username = "jhonny"
        val refreshToken = "Bearer invalid_refresh"

        every { authService.refreshToken(username, refreshToken) } returns null

        val response = authController.refreshToken(username, refreshToken)

        assertNotNull(response)
        assertEquals(HttpStatus.FORBIDDEN, response?.statusCode)
        assertEquals("Invalid client request!", response?.body)
    }
}
