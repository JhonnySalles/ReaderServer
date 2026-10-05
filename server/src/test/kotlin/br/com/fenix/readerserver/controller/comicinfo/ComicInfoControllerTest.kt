package br.com.fenix.readerserver.controller.comicinfo

import br.com.fenix.readerserver.dto.comicinfo.ComicInfoDto
import br.com.fenix.readerserver.dto.data.DataFileDto
import br.com.fenix.readerserver.service.comicinfo.ComicInfoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class ComicInfoControllerTest {

    private lateinit var comicInfoService: ComicInfoService
    private lateinit var comicInfoController: ComicInfoController

    @BeforeEach
    fun setUp() {
        comicInfoService = mockk()
        comicInfoController = ComicInfoController(comicInfoService)
    }

    @Test
    @DisplayName("Deve buscar ComicInfo pelo nome do comic")
    fun `deve buscar comicinfo por comic`() {
        val comic = "Superman"
        val expectedList = listOf(
            ComicInfoDto(id = UUID.randomUUID(), title = "Superman #1", comic = comic)
        )

        every { comicInfoService.findByComic(comic) } returns expectedList

        val response = comicInfoController.findByComic(comic)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.size)
        assertEquals(comic, response.body?.get(0)?.comic)
        verify(exactly = 1) { comicInfoService.findByComic(comic) }
    }

    @Test
    @DisplayName("Deve buscar ComicInfo pela combinação de Série, Volume e Linguagem")
    fun `deve buscar comicinfo por serie volume e linguagem`() {
        val serie = "X-Men"
        val volume = 1f
        val language = "en"
        val expectedList = listOf(
            ComicInfoDto(id = UUID.randomUUID(), series = serie, volume = volume, languageISO = language)
        )

        every { comicInfoService.findBySeriesAndVolumeAndLanguage(serie, volume, language) } returns expectedList

        val response = comicInfoController.findBySeriesAndVolumeAndLanguage(serie, volume, language)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.size)
        assertEquals(serie, response.body?.get(0)?.series)
        assertEquals(volume, response.body?.get(0)?.volume)
        verify(exactly = 1) { comicInfoService.findBySeriesAndVolumeAndLanguage(serie, volume, language) }
    }

    @Test
    @DisplayName("Deve fazer download do ComicInfo.xml com arquivo binário bruto ou gerado dinamicamente")
    fun `deve fazer download do ComicInfo xml com sucesso`() {
        val id = UUID.randomUUID()
        val dto = ComicInfoDto(
            id = id,
            title = "Spider-Man",
            series = "The Amazing Spider-Man",
            number = 1f,
            volume = 1f,
            languageISO = "en"
        )
        val dataFile = DataFileDto(id = UUID.randomUUID(), fileName = "ComicInfo.xml", fileContent = "<ComicInfo><Title>Spider-Man</Title></ComicInfo>")

        every { comicInfoService.findById(id) } returns dto
        every { comicInfoService.findRawDataFiles(id) } returns listOf(dataFile)

        val response = comicInfoController.downloadXml(id)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("<ComicInfo><Title>Spider-Man</Title></ComicInfo>", response.body)
        assertTrue(response.headers.getFirst("Content-Disposition")?.contains("The_Amazing_Spider_Man.xml") == true)
    }
}
