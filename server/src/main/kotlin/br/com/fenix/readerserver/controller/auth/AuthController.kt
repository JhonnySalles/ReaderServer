package br.com.fenix.readerserver.controller.auth

import br.com.fenix.readerserver.dto.auth.CredencialDto
import br.com.fenix.readerserver.service.auth.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Authentication Endpoint", description = "Endpoints para autenticação e renovação de tokens JWT")
@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired
    private lateinit var authService: AuthService

    @Operation(
        summary = "Autenticação de usuário",
        description = "Realiza o login de usuário através de username e password, retornando tokens JWT (Access Token e Refresh Token)."
    )
    @PostMapping(value = ["/signin"])
    fun signin(@RequestBody data: CredencialDto): ResponseEntity<*> {
        return if (checkIfParamsIsNull(data)) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid client request: Username and password are required.")
        } else {
            authService.signin(data)
        }
    }

    @Operation(
        summary = "Renovação de Token (Refresh)",
        description = "Gera um novo access token a partir do refresh token válido fornecido no Header Authorization."
    )
    @PutMapping(value = ["/refresh/{username}"])
    fun refreshToken(
        @PathVariable("username") username: String?,
        @RequestHeader("Authorization") refreshToken: String?
    ): ResponseEntity<*>? {
        return if (checkIfParamsIsNull(username, refreshToken)) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid client request: Username and refresh token are required.")
        } else {
            authService.refreshToken(username!!, refreshToken!!)
                ?: ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!")
        }
    }

    private fun checkIfParamsIsNull(username: String?, refreshToken: String?): Boolean {
        return refreshToken == null || refreshToken.isBlank() || username == null || username.isBlank()
    }

    private fun checkIfParamsIsNull(data: CredencialDto): Boolean {
        return data.username == null || data.username.isBlank() || data.password == null || data.password.isBlank()
    }
}
