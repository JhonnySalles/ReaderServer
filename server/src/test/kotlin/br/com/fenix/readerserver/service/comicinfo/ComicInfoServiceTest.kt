package br.com.fenix.readerserver.service.comicinfo

import br.com.fenix.readerserver.model.comicinfo.ComicInfo
import br.com.fenix.readerserver.repository.comicinfo.ComicInfoRepository
import br.com.fenix.readerserver.repository.data.DataFileRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import java.util.*

class ComicInfoServiceTest {

    private lateinit var comicInfoRepository: ComicInfoRepository
    private lateinit var dataFileRepository: DataFileRepository
    private lateinit var modelMapper: ModelMapper
    private lateinit var comicInfoService: ComicInfoService

    @BeforeEach
    fun setUp() {
        comicInfoRepository = mockk()
        dataFileRepository = mockk()
        modelMapper = ModelMapper()
        comicInfoService = ComicInfoService(comicInfoRepository, dataFileRepository, modelMapper)
    }

    @Test
    @DisplayName("Deve buscar ComicInfo pelo nome do quadrinho/manga")
    fun `deve buscar comicinfo por nome do quadrinho`() {
        val comic = "Batman: Ano Um"
        val comicInfo = ComicInfo(
            id = UUID.randomUUID(),
            comic = comic,
            series = "Batman",
            volume = 1f,
            writer = "Frank Miller"
        )

        every { comicInfoRepository.findByComicIgnoreCase(comic) } returns listOf(comicInfo)

        val result = comicInfoService.findByComic(comic)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(comic, result[0].comic)
        assertEquals("Frank Miller", result[0].writer)
        verify(exactly = 1) { comicInfoRepository.findByComicIgnoreCase(comic) }
    }

    @Test
    @DisplayName("Deve buscar ComicInfo por série, volume e idioma")
    fun `deve buscar comicinfo por serie volume e idioma`() {
        val series = "Naruto"
        val volume = 1f
        val language = "pt-br"
        val comicInfo = ComicInfo(
            id = UUID.randomUUID(),
            series = series,
            volume = volume,
            languageISO = language
        )

        every { comicInfoRepository.findBySeriesAndVolumeAndLanguage(series, volume, language) } returns listOf(comicInfo)

        val result = comicInfoService.findBySeriesAndVolumeAndLanguage(series, volume, language)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(series, result[0].series)
        assertEquals(volume, result[0].volume)
        verify(exactly = 1) { comicInfoRepository.findBySeriesAndVolumeAndLanguage(series, volume, language) }
    }
}
