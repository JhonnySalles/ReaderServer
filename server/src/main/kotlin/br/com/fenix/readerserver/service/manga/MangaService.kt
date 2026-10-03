package br.com.fenix.readerserver.service.manga

import br.com.fenix.readerserver.dto.manga.MangaDto
import br.com.fenix.readerserver.exceptions.InvalidNotFoundException
import br.com.fenix.readerserver.exceptions.RequiredObjectIsNullException
import br.com.fenix.readerserver.mapper.Mapper
import br.com.fenix.readerserver.model.manga.Manga
import br.com.fenix.readerserver.repository.manga.MangaRepository
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
class MangaService(
    private val mangaRepository: MangaRepository,
    private val modelMapper: ModelMapper
) : GenericJpaService<UUID?, Manga, MangaDto>(
    Manga.Companion,
    Manga::class.java,
    MangaDto::class.java
) {

    @Suppress("UNCHECKED_CAST")
    override val repository: JpaRepository<Manga, in UUID?>
        get() = mangaRepository as JpaRepository<Manga, in UUID?>

    override val mapper: Mapper
        get() = Mapper(modelMapper)

    @Transactional(readOnly = true)
    override fun findById(id: UUID?): MangaDto {
        if (id == null) throw RequiredObjectIsNullException("ID cannot be null")
        val entity = mangaRepository.findByIdWithComicInfo(id).orElseThrow {
            InvalidNotFoundException("No records found for ID: $id")
        }
        return mapper.parse(entity, MangaDto::class.java)
    }

    @Transactional(readOnly = true)
    fun findByNome(nome: String, pageable: Pageable, assembler: PagedResourcesAssembler<MangaDto>): PagedModel<EntityModel<MangaDto>> {
        val page: Page<Manga> = mangaRepository.findByNome(nome, pageable)
        val dtoPage: Page<MangaDto> = page.map { mapper.parse(it, MangaDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findByFileName(fileName: String, pageable: Pageable, assembler: PagedResourcesAssembler<MangaDto>): PagedModel<EntityModel<MangaDto>> {
        val page: Page<Manga> = mangaRepository.findByFileName(fileName, pageable)
        val dtoPage: Page<MangaDto> = page.map { mapper.parse(it, MangaDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findByComicInfoId(comicInfoId: UUID): List<MangaDto> {
        return mangaRepository.findByComicInfoId(comicInfoId).map {
            mapper.parse(it, MangaDto::class.java)
        }
    }

    @Transactional(readOnly = true)
    fun findByComicInfoIdPaged(comicInfoId: UUID, pageable: Pageable, assembler: PagedResourcesAssembler<MangaDto>): PagedModel<EntityModel<MangaDto>> {
        val page: Page<Manga> = mangaRepository.findByComicInfoIdPaged(comicInfoId, pageable)
        val dtoPage: Page<MangaDto> = page.map { mapper.parse(it, MangaDto::class.java) }
        return assembler.toModel(dtoPage)
    }
}
