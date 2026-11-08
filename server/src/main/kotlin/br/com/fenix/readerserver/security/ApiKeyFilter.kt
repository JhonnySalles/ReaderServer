package br.com.fenix.readerserver.security

import br.com.fenix.readerserver.service.auth.ApiKeyService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime

@Component
class ApiKeyFilter(
    private val apiKeyService: ApiKeyService
) : OncePerRequestFilter() {

    companion object {
        const val HEADER_API_KEY = "X-API-KEY"
        private val log = LoggerFactory.getLogger(ApiKeyFilter::class.java)
    }

    private val pathMatcher = AntPathMatcher()

    private val excludedPatterns = listOf(
        "/health/**",
        "/api/health/**",
        "/swagger/**",
        "/api/swagger/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/api/swagger-ui/**",
        "/api/swagger-ui.html",
        "/v3/api-docs/**",
        "/api/v3/api-docs/**",
        "/docs/**",
        "/api/docs/**",
        "/documentacao/**",
        "/api/documentacao/**",
        "/webjars/**",
        "/api/webjars/**",
        "/error"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Ignora requisições de preflight CORS (OPTIONS)
        if (request.method.equals("OPTIONS", ignoreCase = true)) {
            filterChain.doFilter(request, response)
            return
        }

        val servletPath = request.servletPath
        if (isExcluded(servletPath)) {
            filterChain.doFilter(request, response)
            return
        }

        val apiKey = request.getHeader(HEADER_API_KEY) ?: request.getHeader(HEADER_API_KEY.lowercase())
        if (apiKey.isNullOrBlank() || !apiKeyService.isValidApiKey(apiKey)) {
            log.warn("Tentativa de acesso não autorizada sem API Key válida para o recurso: $servletPath")
            sendUnauthorizedResponse(request, response, "API Key inválida ou ausente.")
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun isExcluded(path: String): Boolean {
        return excludedPatterns.any { pattern -> pathMatcher.match(pattern, path) }
    }

    private fun sendUnauthorizedResponse(
        request: HttpServletRequest,
        response: HttpServletResponse,
        message: String
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to HttpStatus.UNAUTHORIZED.value(),
            "error" to HttpStatus.UNAUTHORIZED.reasonPhrase,
            "message" to message,
            "path" to request.requestURI
        )

        try {
            val objectMapper = ObjectMapper()
            objectMapper.findAndRegisterModules()
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        } catch (e: Exception) {
            log.error("Erro ao escrever a resposta JSON de erro no ApiKeyFilter.", e)
        }
    }
}

