package br.com.fenix.readerserver.service.auth

import br.com.fenix.readerserver.exceptions.InvalidNotFoundException
import br.com.fenix.readerserver.model.auth.Usuario
import br.com.fenix.readerserver.repository.auth.UsuarioRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class UsuarioServiceTest {

    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var usuarioService: UsuarioService

    @BeforeEach
    fun setUp() {
        usuarioRepository = mockk()
        usuarioService = UsuarioService(usuarioRepository)
    }

    @Test
    @DisplayName("Deve carregar UserDetails com sucesso quando o username existir no banco")
    fun `deve carregar usuario por username com sucesso`() {
        val username = "jhonny"
        val usuario = Usuario(
            id = 1L,
            username = username,
            nome = "Jhonny",
            password = "encoded_password"
        )

        every { usuarioRepository.findByUsername(username) } returns Optional.of(usuario)

        val userDetails = usuarioService.loadUserByUsername(username)

        assertNotNull(userDetails)
        assertEquals(username, userDetails.username)
        assertEquals("encoded_password", userDetails.password)
        assertTrue(userDetails.isEnabled)
        verify(exactly = 1) { usuarioRepository.findByUsername(username) }
    }

    @Test
    @DisplayName("Deve lançar InvalidNotFoundException quando o username não for encontrado")
    fun `deve lancar excecao quando usuario nao for encontrado`() {
        val username = "usuario_inexistente"

        every { usuarioRepository.findByUsername(username) } returns Optional.empty()

        assertThrows<InvalidNotFoundException> {
            usuarioService.loadUserByUsername(username)
        }

        verify(exactly = 1) { usuarioRepository.findByUsername(username) }
    }
}
