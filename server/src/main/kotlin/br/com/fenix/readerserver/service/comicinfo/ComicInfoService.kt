package br.com.fenix.readerserver.service.comicinfo

import br.com.fenix.readerserver.dto.comicinfo.ComicInfoDto
import br.com.fenix.readerserver.mapper.Mapper
import br.com.fenix.readerserver.model.comicinfo.ComicInfo
import br.com.fenix.readerserver.repository.comicinfo.ComicInfoRepository
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

import br.com.fenix.readerserver.repository.data.DataFileRepository

@Service
class ComicInfoService(
    private val comicInfoRepository: ComicInfoRepository,
    private val dataFileRepository: DataFileRepository,
    private val modelMapper: ModelMapper
) : GenericJpaService<UUID?, ComicInfo, ComicInfoDto>(
    ComicInfo.Companion,
    ComicInfo::class.java,
    ComicInfoDto::class.java
) {

    @Suppress("UNCHECKED_CAST")
    override val repository: JpaRepository<ComicInfo, in UUID?>
        get() = comicInfoRepository as JpaRepository<ComicInfo, in UUID?>

    override val mapper: Mapper
        get() = Mapper(modelMapper)

    @Transactional(readOnly = true)
    fun findBySeries(series: String, pageable: Pageable, assembler: PagedResourcesAssembler<ComicInfoDto>): PagedModel<EntityModel<ComicInfoDto>> {
        val page: Page<ComicInfo> = comicInfoRepository.findBySeriesContaining(series, pageable)
        val dtoPage: Page<ComicInfoDto> = page.map { mapper.parse(it, ComicInfoDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findByTitle(title: String, pageable: Pageable, assembler: PagedResourcesAssembler<ComicInfoDto>): PagedModel<EntityModel<ComicInfoDto>> {
        val page: Page<ComicInfo> = comicInfoRepository.findByTitleContaining(title, pageable)
        val dtoPage: Page<ComicInfoDto> = page.map { mapper.parse(it, ComicInfoDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findByComic(comic: String): List<ComicInfoDto> {
        return comicInfoRepository.findByComicIgnoreCase(comic).map {
            mapper.parse(it, ComicInfoDto::class.java)
        }
    }

    @Transactional(readOnly = true)
    fun findBySeriesAndVolumeAndLanguage(series: String, volume: Float, language: String): List<ComicInfoDto> {
        return comicInfoRepository.findBySeriesAndVolumeAndLanguage(series, volume, language).map {
            mapper.parse(it, ComicInfoDto::class.java)
        }
    }

    @Transactional(readOnly = true)
    fun findByTitleAndVolumeAndLanguage(title: String, volume: Float, language: String): List<ComicInfoDto> {
        return comicInfoRepository.findByTitleAndVolumeAndLanguage(title, volume, language).map {
            mapper.parse(it, ComicInfoDto::class.java)
        }
    }

    @Transactional(readOnly = true)
    fun searchAdvanced(filter: br.com.fenix.readerserver.dto.comicinfo.ComicInfoSearchFilterDto, pageable: Pageable, assembler: PagedResourcesAssembler<ComicInfoDto>): PagedModel<EntityModel<ComicInfoDto>> {
        val spec = br.com.fenix.readerserver.repository.comicinfo.ComicInfoSpecification.withFilters(filter)
        val page: Page<ComicInfo> = comicInfoRepository.findAll(spec, pageable)
        val dtoPage: Page<ComicInfoDto> = page.map { mapper.parse(it, ComicInfoDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findRawDataFiles(comicInfoId: UUID): List<br.com.fenix.readerserver.dto.data.DataFileDto> {
        return dataFileRepository.findByComicInfoId(comicInfoId).map {
            mapper.parse(it, br.com.fenix.readerserver.dto.data.DataFileDto::class.java)
        }
    }
}
