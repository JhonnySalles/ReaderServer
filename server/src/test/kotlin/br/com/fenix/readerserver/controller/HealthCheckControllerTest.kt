package br.com.fenix.readerserver.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class HealthCheckControllerTest {

    private val controller = HealthCheckController()

    @Test
    @DisplayName("Deve retornar status UP e nome do serviço")
    fun `deve retornar status UP`() {
        val response = controller.healthCheck()

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body
        assertNotNull(body)
        assertEquals("UP", body?.get("status"))
        assertEquals("ReaderServer API", body?.get("service"))
        assertNotNull(body?.get("timestamp"))
    }
}
