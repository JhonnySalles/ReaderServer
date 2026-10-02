package br.com.fenix.readerserver.service.auth

import br.com.fenix.readerserver.dto.auth.CredencialDto
import br.com.fenix.readerserver.dto.auth.TokenDto
import br.com.fenix.readerserver.repository.auth.UsuarioRepository
import br.com.fenix.readerserver.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthService {

    companion object {
        private val oLog = LoggerFactory.getLogger(AuthService::class.java.name)
    }

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var tokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var repository: UsuarioRepository

    fun signin(data: CredencialDto): ResponseEntity<TokenDto> {
        return try {
            val username = data.username ?: throw BadCredentialsException("Username is required")
            val password = data.password ?: throw BadCredentialsException("Password is required")
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
            val user = repository.findByUsername(username).orElseThrow { UsernameNotFoundException("Username $username not found!") }
            val tokenResponse = tokenProvider.createAccessToken(username, user.getRoles())
            ResponseEntity.ok(tokenResponse)
        } catch (e: Exception) {
            oLog.error("Error when signing in: ${e.message}")
            throw BadCredentialsException("Invalid username/password supplied!")
        }
    }

    fun refreshToken(username: String, refreshToken: String): ResponseEntity<TokenDto>? {
        if (!repository.findByUsername(username).isPresent) {
            throw UsernameNotFoundException("Username $username not found!")
        }
        val tokenResponse = tokenProvider.refreshToken(refreshToken)
        return ResponseEntity.ok(tokenResponse)
    }
}
