package br.com.fenix.readerserver.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Health Check", description = "Endpoint para verificação do status da API")
@RestController
@RequestMapping("/health")
class HealthCheckController {

    @Operation(summary = "Health check", description = "Verifica se a API está online e respondendo adequadamente.")
    @GetMapping
    fun healthCheck(): ResponseEntity<Map<String, Any>> {
        val status = mapOf(
            "status" to "UP",
            "service" to "ReaderServer API",
            "timestamp" to LocalDateTime.now().toString()
        )
        return ResponseEntity.ok(status)
    }
}
