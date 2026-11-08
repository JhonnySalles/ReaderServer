package br.com.fenix.readerserver.service.auth

import br.com.fenix.readerserver.repository.auth.SegurancaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ApiKeyService(
    @Autowired private val repository: SegurancaRepository
) {

    private val validKeysCache = ConcurrentHashMap<String, Long>()
    private val cacheTtlMs = 60_000L // 1 minuto de cache

    fun isValidApiKey(apiKey: String?): Boolean {
        if (apiKey.isNullOrBlank()) {
            return false
        }
        val cleanKey = apiKey.trim()

        val now = System.currentTimeMillis()
        val cachedTime = validKeysCache[cleanKey]
        if (cachedTime != null && (now - cachedTime) < cacheTtlMs) {
            return true
        }

        val exists = repository.existsByApiKeyAndAtivoTrue(cleanKey)
        if (exists) {
            validKeysCache[cleanKey] = now
        } else {
            validKeysCache.remove(cleanKey)
        }
        return exists
    }

    fun invalidateCache() {
        validKeysCache.clear()
    }
}

