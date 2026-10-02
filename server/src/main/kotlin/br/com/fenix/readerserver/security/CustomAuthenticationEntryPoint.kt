package br.com.fenix.readerserver.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    companion object {
        private val oLog = LoggerFactory.getLogger(CustomAuthenticationEntryPoint::class.java)
    }

    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        oLog.warn("Falha de autenticação não autorizada: ${authException.message}")

        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to HttpStatus.UNAUTHORIZED.value(),
            "error" to "Unauthorized",
            "message" to "Token de acesso inválido, ausente ou expirado. Por favor, autentique-se.",
            "path" to request.requestURI
        )

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val objectMapper = ObjectMapper()
        objectMapper.findAndRegisterModules()
        objectMapper.writeValue(response.writer, errorResponse)
    }
}
