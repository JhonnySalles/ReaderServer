package br.com.fenix.readerserver.service.opf

import br.com.fenix.readerserver.model.opf.Opf
import br.com.fenix.readerserver.repository.data.DataFileRepository
import br.com.fenix.readerserver.repository.opf.OpfRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import java.util.*

class OpfServiceTest {

    private lateinit var opfRepository: OpfRepository
    private lateinit var dataFileRepository: DataFileRepository
    private lateinit var modelMapper: ModelMapper
    private lateinit var opfService: OpfService

    @BeforeEach
    fun setUp() {
        opfRepository = mockk()
        dataFileRepository = mockk()
        modelMapper = ModelMapper()
        opfService = OpfService(opfRepository, dataFileRepository, modelMapper)
    }

    @Test
    @DisplayName("Deve buscar OPF pelo título exato")
    fun `deve buscar opf por titulo exato`() {
        val title = "The Pragmatic Programmer"
        val opf = Opf(
            id = UUID.randomUUID(),
            title = title,
            creator = "Andy Hunt",
            publisher = "Addison-Wesley"
        )

        every { opfRepository.findByTitleIgnoreCase(title) } returns listOf(opf)

        val result = opfService.findByTitleExact(title)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(title, result[0].title)
        assertEquals("Andy Hunt", result[0].creator)
        verify(exactly = 1) { opfRepository.findByTitleIgnoreCase(title) }
    }

    @Test
    @DisplayName("Deve buscar OPF por série, volume e idioma")
    fun `deve buscar opf por serie volume e idioma`() {
        val series = "Harry Potter"
        val volume = 1f
        val language = "pt"
        val opf = Opf(
            id = UUID.randomUUID(),
            title = "A Pedra Filosofal",
            series = series,
            volume = volume,
            language = language
        )

        every { opfRepository.findBySeriesAndVolumeAndLanguage(series, volume, language) } returns listOf(opf)

        val result = opfService.findBySeriesAndVolumeAndLanguage(series, volume, language)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(series, result[0].series)
        assertEquals(volume, result[0].volume)
        assertEquals(language, result[0].language)
        verify(exactly = 1) { opfRepository.findBySeriesAndVolumeAndLanguage(series, volume, language) }
    }
}
