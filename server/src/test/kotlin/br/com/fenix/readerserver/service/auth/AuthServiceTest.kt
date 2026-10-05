package br.com.fenix.readerserver.service.auth

import br.com.fenix.readerserver.dto.auth.CredencialDto
import br.com.fenix.readerserver.dto.auth.TokenDto
import br.com.fenix.readerserver.model.auth.Permissao
import br.com.fenix.readerserver.model.auth.Usuario
import br.com.fenix.readerserver.repository.auth.UsuarioRepository
import br.com.fenix.readerserver.security.JwtTokenProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

class AuthServiceTest {

    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var tokenProvider: JwtTokenProvider
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authenticationManager = mockk()
        tokenProvider = mockk()
        usuarioRepository = mockk()

        authService = AuthService()
        ReflectionTestUtils.setField(authService, "authenticationManager", authenticationManager)
        ReflectionTestUtils.setField(authService, "tokenProvider", tokenProvider)
        ReflectionTestUtils.setField(authService, "repository", usuarioRepository)
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando as credenciais forem válidas")
    fun `deve autenticar com sucesso`() {
        val credenciais = CredencialDto("jhonny", "123456")
        val permissao = Permissao(id = 1L, descricao = "ROLE_ADMIN")
        val usuario = Usuario(
            id = 1L,
            username = "jhonny",
            nome = "Jhonny",
            password = "encoded_password",
            permissoes = mutableListOf(permissao)
        )
        val expectedToken = TokenDto(
            username = "jhonny",
            authenticated = true,
            created = Date(),
            expiration = Date(),
            accessToken = "valid_access_token",
            refreshToken = "valid_refresh_token"
        )

        every { authenticationManager.authenticate(any()) } returns UsernamePasswordAuthenticationToken("jhonny", "123456")
        every { usuarioRepository.findByUsername("jhonny") } returns Optional.of(usuario)
        every { tokenProvider.createAccessToken("jhonny", listOf("ROLE_ADMIN")) } returns expectedToken

        val response = authService.signin(credenciais)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedToken, response.body)
        verify(exactly = 1) { authenticationManager.authenticate(any()) }
        verify(exactly = 1) { usuarioRepository.findByUsername("jhonny") }
        verify(exactly = 1) { tokenProvider.createAccessToken("jhonny", listOf("ROLE_ADMIN")) }
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando a autenticação falhar")
    fun `deve lancar excecao quando credenciais forem invalidas`() {
        val credenciais = CredencialDto("jhonny", "senha_errada")

        every { authenticationManager.authenticate(any()) } throws BadCredentialsException("Bad credentials")

        assertThrows<BadCredentialsException> {
            authService.signin(credenciais)
        }
    }

    @Test
    @DisplayName("Deve renovar o token com sucesso quando o refresh token e o usuário forem válidos")
    fun `deve renovar token com sucesso`() {
        val username = "jhonny"
        val refreshToken = "valid_refresh_token"
        val usuario = Usuario(
            id = 1L,
            username = username,
            nome = "Jhonny",
            password = "password"
        )
        val expectedToken = TokenDto(
            username = username,
            authenticated = true,
            created = Date(),
            expiration = Date(),
            accessToken = "new_access_token",
            refreshToken = "new_refresh_token"
        )

        every { usuarioRepository.findByUsername(username) } returns Optional.of(usuario)
        every { tokenProvider.refreshToken(refreshToken) } returns expectedToken

        val response = authService.refreshToken(username, refreshToken)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response?.statusCode)
        assertEquals(expectedToken, response?.body)
        verify(exactly = 1) { usuarioRepository.findByUsername(username) }
        verify(exactly = 1) { tokenProvider.refreshToken(refreshToken) }
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException ao renovar token de usuário inexistente")
    fun `deve lancar excecao ao renovar token de usuario inexistente`() {
        val username = "usuario_inexistente"
        every { usuarioRepository.findByUsername(username) } returns Optional.empty()

        assertThrows<UsernameNotFoundException> {
            authService.refreshToken(username, "any_token")
        }
    }
}
