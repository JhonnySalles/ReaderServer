package br.com.fenix.readerserver.service.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApiKeyService(
    @Value("\${security.apikey:\${security.api-key:}}")
    private val configuredApiKey: String = ""
) {

    fun isValidApiKey(apiKey: String?): Boolean {
        if (apiKey.isNullOrBlank() || configuredApiKey.isBlank()) {
            return false
        }
        val cleanKey = apiKey.trim()
        val allowedKeys = configuredApiKey
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return allowedKeys.contains(cleanKey)
    }
}
