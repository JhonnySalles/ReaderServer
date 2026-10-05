package br.com.fenix.readerserver.security

import jakarta.servlet.http.HttpServletRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun setUp() {
        jwtTokenProvider = JwtTokenProvider()
        // Inicializa o secretKey e o algoritmo HMAC256
        ReflectionTestUtils.invokeMethod<Unit>(jwtTokenProvider, "init")
    }

    @Test
    @DisplayName("Deve criar Access Token e Refresh Token com valores válidos")
    fun `deve criar access token e refresh token`() {
        val username = "jhonny"
        val roles = listOf("ROLE_ADMIN", "ROLE_USER")

        val tokenDto = jwtTokenProvider.createAccessToken(username, roles)

        assertNotNull(tokenDto)
        assertEquals(username, tokenDto.username)
        assertTrue(tokenDto.authenticated)
        assertNotNull(tokenDto.accessToken)
        assertNotNull(tokenDto.refreshToken)
        assertTrue(tokenDto.accessToken.isNotBlank())
        assertTrue(tokenDto.refreshToken.isNotBlank())
    }

    @Test
    @DisplayName("Deve validar um token gerado com sucesso")
    fun `deve validar token valido`() {
        val tokenDto = jwtTokenProvider.createAccessToken("usuario_teste", listOf("ROLE_USER"))

        val isValid = jwtTokenProvider.validateToken(tokenDto.accessToken)

        assertTrue(isValid)
    }

    @Test
    @DisplayName("Deve retornar falso ao validar um token inválido ou corrompido")
    fun `deve retornar falso para token invalido`() {
        val isValid = jwtTokenProvider.validateToken("token_invalido_qualquer")

        assertFalse(isValid)
    }

    @Test
    @DisplayName("Deve extrair a autenticação do token com o username e roles corretos")
    fun `deve extrair autenticacao do token`() {
        val username = "admin"
        val roles = listOf("ROLE_ADMIN")
        val tokenDto = jwtTokenProvider.createAccessToken(username, roles)

        val authentication = jwtTokenProvider.getAuthentication(tokenDto.accessToken)

        assertNotNull(authentication)
        assertEquals(username, authentication.name)
        val authorities = authentication.authorities.map { it.authority }
        assertTrue(authorities.contains("ROLE_ADMIN"))
    }

    @Test
    @DisplayName("Deve resolver o token a partir do header Authorization com prefixo Bearer")
    fun `deve resolver token do header Authorization`() {
        val request = mockk<HttpServletRequest>()
        every { request.getHeader("Authorization") } returns "Bearer meu_token_jwt"

        val resolved = jwtTokenProvider.resolveToken(request)

        assertEquals("meu_token_jwt", resolved)
    }

    @Test
    @DisplayName("Deve retornar null ao resolver token se o header Authorization for ausente")
    fun `deve retornar null se header Authorization for ausente`() {
        val request = mockk<HttpServletRequest>()
        every { request.getHeader("Authorization") } returns null

        val resolved = jwtTokenProvider.resolveToken(request)

        assertNull(resolved)
    }

    @Test
    @DisplayName("Deve renovar o token através do refresh token")
    fun `deve renovar token via refresh token`() {
        val username = "jhonny"
        val roles = listOf("ROLE_USER")
        val originalToken = jwtTokenProvider.createAccessToken(username, roles)

        val refreshedToken = jwtTokenProvider.refreshToken(originalToken.refreshToken)

        assertNotNull(refreshedToken)
        assertEquals(username, refreshedToken.username)
        assertTrue(jwtTokenProvider.validateToken(refreshedToken.accessToken))
    }
}
