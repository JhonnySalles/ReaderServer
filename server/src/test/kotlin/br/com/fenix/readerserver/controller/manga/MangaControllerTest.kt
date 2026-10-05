package br.com.fenix.readerserver.controller.manga

import br.com.fenix.readerserver.dto.comicinfo.ComicInfoDto
import br.com.fenix.readerserver.dto.data.DataFileDto
import br.com.fenix.readerserver.dto.manga.MangaDto
import br.com.fenix.readerserver.service.manga.MangaService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class MangaControllerTest {

    private lateinit var mangaService: MangaService
    private lateinit var mangaController: MangaController

    @BeforeEach
    fun setUp() {
        mangaService = mockk()
        mangaController = MangaController(mangaService)
    }

    @Test
    @DisplayName("Deve buscar um único manga por nome de arquivo exato")
    fun `deve buscar manga por nome de arquivo exato`() {
        val fileName = "naruto_01.cbz"
        val expectedDto = MangaDto(
            id = UUID.randomUUID(),
            nome = "Naruto",
            fileName = fileName
        )

        every { mangaService.findSingleByFileName(fileName) } returns expectedDto

        val response = mangaController.findSingleByFileName(fileName)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedDto, response.body)
        verify(exactly = 1) { mangaService.findSingleByFileName(fileName) }
    }

    @Test
    @DisplayName("Deve buscar mangas por série e volume")
    fun `deve buscar mangas por serie e volume`() {
        val serie = "Dragon Ball"
        val volume = 1f
        val expectedList = listOf(
            MangaDto(id = UUID.randomUUID(), nome = "Dragon Ball 1", serie = serie, volume = volume)
        )

        every { mangaService.findBySerieAndVolume(serie, volume) } returns expectedList

        val response = mangaController.findBySerieAndVolume(serie, volume)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.size)
        assertEquals(serie, response.body?.get(0)?.serie)
        verify(exactly = 1) { mangaService.findBySerieAndVolume(serie, volume) }
    }

    @Test
    @DisplayName("Deve buscar mangas vinculados a um ComicInfo ID")
    fun `deve buscar mangas por comicInfoId`() {
        val comicInfoId = UUID.randomUUID()
        val expectedList = listOf(
            MangaDto(id = UUID.randomUUID(), nome = "Manga com ComicInfo", comicInfoId = comicInfoId)
        )

        every { mangaService.findByComicInfoId(comicInfoId) } returns expectedList

        val response = mangaController.findByComicInfoId(comicInfoId)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.size)
        assertEquals(comicInfoId, response.body?.get(0)?.comicInfoId)
        verify(exactly = 1) { mangaService.findByComicInfoId(comicInfoId) }
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found no download quando o manga não possui comicInfoId")
    fun `deve retornar 404 no download se comicInfoId for nulo`() {
        val id = UUID.randomUUID()
        val mangaDto = MangaDto(id = id, nome = "Sem ComicInfo", comicInfoId = null)

        every { mangaService.findById(id) } returns mangaDto

        val response = mangaController.downloadLinkedContent(id)

        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    @DisplayName("Deve retornar arquivo ComicInfo XML com cabeçalho Content-Disposition no download")
    fun `deve fazer download do conteudo ComicInfo vinculado com sucesso`() {
        val id = UUID.randomUUID()
        val comicInfoId = UUID.randomUUID()
        val comicInfoDto = ComicInfoDto(id = comicInfoId, title = "Naruto", writer = "Masashi Kishimoto")
        val mangaDto = MangaDto(id = id, nome = "Naruto", comicInfoId = comicInfoId, comicInfo = comicInfoDto)
        val dataFile = DataFileDto(id = UUID.randomUUID(), fileName = "ComicInfo.xml", fileContent = "<ComicInfo>test</ComicInfo>")

        every { mangaService.findById(id) } returns mangaDto
        every { mangaService.findRawDataFilesByComicInfo(comicInfoId) } returns listOf(dataFile)

        val response = mangaController.downloadLinkedContent(id)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("<ComicInfo>test</ComicInfo>", response.body)
        assertTrue(response.headers.getFirst("Content-Disposition")?.contains("Naruto.xml") == true)
    }
}
