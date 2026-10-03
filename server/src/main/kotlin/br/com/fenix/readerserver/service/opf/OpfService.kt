package br.com.fenix.readerserver.service.opf

import br.com.fenix.readerserver.dto.opf.OpfDto
import br.com.fenix.readerserver.mapper.Mapper
import br.com.fenix.readerserver.model.opf.Opf
import br.com.fenix.readerserver.repository.opf.OpfRepository
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
class OpfService(
    private val opfRepository: OpfRepository,
    private val modelMapper: ModelMapper
) : GenericJpaService<UUID?, Opf, OpfDto>(
    Opf.Companion,
    Opf::class.java,
    OpfDto::class.java
) {

    @Suppress("UNCHECKED_CAST")
    override val repository: JpaRepository<Opf, in UUID?>
        get() = opfRepository as JpaRepository<Opf, in UUID?>

    override val mapper: Mapper
        get() = Mapper(modelMapper)

    @Transactional(readOnly = true)
    fun findByTitle(title: String, pageable: Pageable, assembler: PagedResourcesAssembler<OpfDto>): PagedModel<EntityModel<OpfDto>> {
        val page: Page<Opf> = opfRepository.findByTitleContaining(title, pageable)
        val dtoPage: Page<OpfDto> = page.map { mapper.parse(it, OpfDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findByCreator(creator: String, pageable: Pageable, assembler: PagedResourcesAssembler<OpfDto>): PagedModel<EntityModel<OpfDto>> {
        val page: Page<Opf> = opfRepository.findByCreatorContaining(creator, pageable)
        val dtoPage: Page<OpfDto> = page.map { mapper.parse(it, OpfDto::class.java) }
        return assembler.toModel(dtoPage)
    }

    @Transactional(readOnly = true)
    fun findBySeries(series: String, pageable: Pageable, assembler: PagedResourcesAssembler<OpfDto>): PagedModel<EntityModel<OpfDto>> {
        val page: Page<Opf> = opfRepository.findBySeriesContaining(series, pageable)
        val dtoPage: Page<OpfDto> = page.map { mapper.parse(it, OpfDto::class.java) }
        return assembler.toModel(dtoPage)
    }
}
