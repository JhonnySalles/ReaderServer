package br.com.fenix.readerserver.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User

class JwtTokenFilterTest {

    private lateinit var tokenProvider: JwtTokenProvider
    private lateinit var jwtTokenFilter: JwtTokenFilter
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var chain: FilterChain

    @BeforeEach
    fun setUp() {
        tokenProvider = mockk()
        jwtTokenFilter = JwtTokenFilter(tokenProvider)
        request = mockk()
        response = mockk()
        chain = mockk(relaxed = true)
        SecurityContextHolder.clearContext()
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    @DisplayName("Deve autenticar o usuário e preencher o SecurityContext quando o token for válido")
    fun `deve autenticar quando token for valido`() {
        val token = "token_valido"
        val user = User("jhonny", "", listOf(SimpleGrantedAuthority("ROLE_USER")))
        val auth = UsernamePasswordAuthenticationToken(user, "", user.authorities)

        every { tokenProvider.resolveToken(request) } returns token
        every { tokenProvider.getAuthentication(token) } returns auth

        jwtTokenFilter.doFilter(request, response, chain)

        val currentAuth = SecurityContextHolder.getContext().authentication
        assertNotNull(currentAuth)
        assertEquals("jhonny", currentAuth.name)
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    @Test
    @DisplayName("Deve manter o contexto anônimo e continuar a cadeia quando não houver token")
    fun `deve continuar cadeia quando nao houver token`() {
        every { tokenProvider.resolveToken(request) } returns null

        jwtTokenFilter.doFilter(request, response, chain)

        assertNull(SecurityContextHolder.getContext().authentication)
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    @Test
    @DisplayName("Deve limpar o SecurityContextHolder e propagar exceção quando o token for inválido")
    fun `deve limpar contexto quando token falhar na autenticacao`() {
        val token = "token_invalido"

        every { tokenProvider.resolveToken(request) } returns token
        every { tokenProvider.getAuthentication(token) } throws RuntimeException("Token corrompido")

        assertThrows<RuntimeException> {
            jwtTokenFilter.doFilter(request, response, chain)
        }

        assertNull(SecurityContextHolder.getContext().authentication)
    }
}
