package br.com.fenix.readerserver.service.data

import br.com.fenix.readerserver.model.data.DataFile
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

class DataFileServiceTest {

    private lateinit var dataFileRepository: DataFileRepository
    private lateinit var modelMapper: ModelMapper
    private lateinit var dataFileService: DataFileService

    @BeforeEach
    fun setUp() {
        dataFileRepository = mockk()
        modelMapper = ModelMapper()
        dataFileService = DataFileService(dataFileRepository, modelMapper)
    }

    @Test
    @DisplayName("Deve buscar arquivos de dados associados a um ComicInfo ID")
    fun `deve buscar data files por comicInfoId`() {
        val comicInfoId = UUID.randomUUID()
        val files = listOf(
            DataFile(id = UUID.randomUUID(), fileName = "page_01.jpg", fileContent = "conteudo_base64")
        )

        every { dataFileRepository.findByComicInfoId(comicInfoId) } returns files

        val result = dataFileService.findByComicInfoId(comicInfoId)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("page_01.jpg", result[0].fileName)
        verify(exactly = 1) { dataFileRepository.findByComicInfoId(comicInfoId) }
    }

    @Test
    @DisplayName("Deve buscar arquivos de dados associados a um Opf ID")
    fun `deve buscar data files por opfId`() {
        val opfId = UUID.randomUUID()
        val files = listOf(
            DataFile(id = UUID.randomUUID(), fileName = "chapter_01.xhtml", fileContent = "<html/>")
        )

        every { dataFileRepository.findByOpfId(opfId) } returns files

        val result = dataFileService.findByOpfId(opfId)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("chapter_01.xhtml", result[0].fileName)
        verify(exactly = 1) { dataFileRepository.findByOpfId(opfId) }
    }
}
