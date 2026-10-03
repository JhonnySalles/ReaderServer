package br.com.fenix.readerserver.service.data

import br.com.fenix.readerserver.dto.data.DataFileDto
import br.com.fenix.readerserver.mapper.Mapper
import br.com.fenix.readerserver.model.data.DataFile
import br.com.fenix.readerserver.repository.data.DataFileRepository
import br.com.fenix.readerserver.service.GenericJpaService
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DataFileService(
    private val dataFileRepository: DataFileRepository,
    private val modelMapper: ModelMapper
) : GenericJpaService<UUID?, DataFile, DataFileDto>(
    DataFile.Companion,
    DataFile::class.java,
    DataFileDto::class.java
) {

    @Suppress("UNCHECKED_CAST")
    override val repository: JpaRepository<DataFile, in UUID?>
        get() = dataFileRepository as JpaRepository<DataFile, in UUID?>

    override val mapper: Mapper
        get() = Mapper(modelMapper)

    @Transactional(readOnly = true)
    fun findByComicInfoId(comicInfoId: UUID): List<DataFileDto> {
        return dataFileRepository.findByComicInfoId(comicInfoId).map {
            mapper.parse(it, DataFileDto::class.java)
        }
    }

    @Transactional(readOnly = true)
    fun findByOpfId(opfId: UUID): List<DataFileDto> {
        return dataFileRepository.findByOpfId(opfId).map {
            mapper.parse(it, DataFileDto::class.java)
        }
    }

    @Transactional(readOnly = true)
    fun findByComicInfoIdPaged(comicInfoId: UUID, pageable: Pageable, assembler: PagedResourcesAssembler<DataFileDto>): PagedModel<EntityModel<DataFileDto>> {
        val page: Page<DataFile> = dataFileRepository.findByComicInfoIdPaged(comicInfoId, pageable)
        val dtoPage: Page<DataFileDto> = page.map { mapper.parse(it, DataFileDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findByOpfIdPaged(opfId: UUID, pageable: Pageable, assembler: PagedResourcesAssembler<DataFileDto>): PagedModel<EntityModel<DataFileDto>> {
        val page: Page<DataFile> = dataFileRepository.findByOpfIdPaged(opfId, pageable)
        val dtoPage: Page<DataFileDto> = page.map { mapper.parse(it, DataFileDto::class.java) }
        return assembler.toModel(dtoPage)
    }
}
