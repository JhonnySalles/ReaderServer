package br.com.fenix.readerserver.service.auth

import br.com.fenix.readerserver.exceptions.InvalidNotFoundException
import br.com.fenix.readerserver.repository.auth.UsuarioRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UsuarioService(var repository: UsuarioRepository) : UserDetailsService {

    companion object {
        private val oLog = LoggerFactory.getLogger(UsuarioService::class.java.name)
    }

    @Throws(InvalidNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        oLog.info("Find one user by username $username!")
        val usuario = repository.findByUsername(username)
        return usuario.orElseThrow { throw InvalidNotFoundException("Username $username not found!") }
    }
}
