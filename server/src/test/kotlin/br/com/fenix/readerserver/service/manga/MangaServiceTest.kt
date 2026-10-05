package br.com.fenix.readerserver.service.manga

import br.com.fenix.readerserver.dto.manga.MangaDto
import br.com.fenix.readerserver.exceptions.InvalidNotFoundException
import br.com.fenix.readerserver.exceptions.RequiredObjectIsNullException
import br.com.fenix.readerserver.model.manga.Manga
import br.com.fenix.readerserver.repository.data.DataFileRepository
import br.com.fenix.readerserver.repository.manga.MangaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.modelmapper.ModelMapper
import java.util.*

class MangaServiceTest {

    private lateinit var mangaRepository: MangaRepository
    private lateinit var dataFileRepository: DataFileRepository
    private lateinit var modelMapper: ModelMapper
    private lateinit var mangaService: MangaService

    @BeforeEach
    fun setUp() {
        mangaRepository = mockk()
        dataFileRepository = mockk()
        modelMapper = ModelMapper()
        mangaService = MangaService(mangaRepository, dataFileRepository, modelMapper)
    }

    @Test
    @DisplayName("Deve lançar RequiredObjectIsNullException quando o ID informado for nulo")
    fun `deve lancar excecao quando id for nulo`() {
        assertThrows<RequiredObjectIsNullException> {
            mangaService.findById(null)
        }
    }

    @Test
    @DisplayName("Deve lançar InvalidNotFoundException quando o mangá não for encontrado pelo ID")
    fun `deve lancar excecao quando manga nao for encontrado`() {
        val id = UUID.randomUUID()
        every { mangaRepository.findByIdWithComicInfo(id) } returns Optional.empty()

        assertThrows<InvalidNotFoundException> {
            mangaService.findById(id)
        }

        verify(exactly = 1) { mangaRepository.findByIdWithComicInfo(id) }
    }

    @Test
    @DisplayName("Deve retornar MangaDto com sucesso quando o mangá for encontrado pelo ID")
    fun `deve retornar MangaDto quando encontrado por ID`() {
        val id = UUID.randomUUID()
        val manga = Manga(
            id = id,
            nome = "Bleach",
            fileName = "bleach_vol01.cbz",
            serie = "Bleach",
            volume = 1f
        )

        every { mangaRepository.findByIdWithComicInfo(id) } returns Optional.of(manga)

        val result = mangaService.findById(id)

        assertNotNull(result)
        assertEquals("Bleach", result.nome)
        assertEquals("bleach_vol01.cbz", result.fileName)
        verify(exactly = 1) { mangaRepository.findByIdWithComicInfo(id) }
    }

    @Test
    @DisplayName("Deve retornar MangaDto quando encontrado pelo nome exato do arquivo")
    fun `deve retornar MangaDto quando encontrado por fileName`() {
        val fileName = "naruto_vol01.cbz"
        val manga = Manga(
            id = UUID.randomUUID(),
            nome = "Naruto",
            fileName = fileName
        )

        every { mangaRepository.findByFileNameExact(fileName) } returns Optional.of(manga)

        val result = mangaService.findSingleByFileName(fileName)

        assertNotNull(result)
        assertEquals("Naruto", result.nome)
        assertEquals(fileName, result.fileName)
        verify(exactly = 1) { mangaRepository.findByFileNameExact(fileName) }
    }

    @Test
    @DisplayName("Deve retornar lista de MangaDto filtrada por Série e Volume")
    fun `deve retornar lista filtrada por serie e volume`() {
        val serie = "One Piece"
        val volume = 100f
        val mangas = listOf(
            Manga(id = UUID.randomUUID(), nome = "One Piece 100", serie = serie, volume = volume)
        )

        every { mangaRepository.findBySerieAndVolume(serie, volume) } returns mangas

        val result = mangaService.findBySerieAndVolume(serie, volume)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(serie, result[0].serie)
        assertEquals(volume, result[0].volume)
        verify(exactly = 1) { mangaRepository.findBySerieAndVolume(serie, volume) }
    }
}
