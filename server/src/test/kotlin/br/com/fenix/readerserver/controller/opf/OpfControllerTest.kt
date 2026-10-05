package br.com.fenix.readerserver.controller.opf

import br.com.fenix.readerserver.dto.data.DataFileDto
import br.com.fenix.readerserver.dto.opf.OpfDto
import br.com.fenix.readerserver.service.opf.OpfService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class OpfControllerTest {

    private lateinit var opfService: OpfService
    private lateinit var opfController: OpfController

    @BeforeEach
    fun setUp() {
        opfService = mockk()
        opfController = OpfController(opfService)
    }

    @Test
    @DisplayName("Deve buscar OPF pelo título exato")
    fun `deve buscar opf por titulo exato`() {
        val title = "The Hobbit"
        val expectedList = listOf(
            OpfDto(id = UUID.randomUUID(), title = title, creator = "J.R.R. Tolkien")
        )

        every { opfService.findByTitleExact(title) } returns expectedList

        val response = opfController.findByTitleExact(title)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.size)
        assertEquals(title, response.body?.get(0)?.title)
        verify(exactly = 1) { opfService.findByTitleExact(title) }
    }

    @Test
    @DisplayName("Deve buscar OPF por série, volume e idioma")
    fun `deve buscar opf por serie volume e linguagem`() {
        val serie = "O Senhor dos Anéis"
        val volume = 1f
        val language = "pt"
        val expectedList = listOf(
            OpfDto(id = UUID.randomUUID(), title = "A Sociedade do Anel", series = serie, volume = volume, language = language)
        )

        every { opfService.findBySeriesAndVolumeAndLanguage(serie, volume, language) } returns expectedList

        val response = opfController.findBySeriesAndVolumeAndLanguage(serie, volume, language)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.size)
        assertEquals(serie, response.body?.get(0)?.series)
        verify(exactly = 1) { opfService.findBySeriesAndVolumeAndLanguage(serie, volume, language) }
    }

    @Test
    @DisplayName("Deve fazer download do arquivo .opf com cabeçalho Content-Disposition")
    fun `deve fazer download do arquivo opf com sucesso`() {
        val id = UUID.randomUUID()
        val dto = OpfDto(
            id = id,
            title = "Duna",
            creator = "Frank Herbert",
            publisher = "Aleph",
            language = "pt"
        )
        val dataFile = DataFileDto(id = UUID.randomUUID(), fileName = "content.opf", fileContent = "<package><metadata><dc:title>Duna</dc:title></metadata></package>")

        every { opfService.findById(id) } returns dto
        every { opfService.findRawDataFiles(id) } returns listOf(dataFile)

        val response = opfController.downloadXml(id)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("<package><metadata><dc:title>Duna</dc:title></metadata></package>", response.body)
        assertTrue(response.headers.getFirst("Content-Disposition")?.contains("Duna.opf") == true)
    }
}
