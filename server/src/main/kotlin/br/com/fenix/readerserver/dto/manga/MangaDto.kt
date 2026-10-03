package br.com.fenix.readerserver.dto.manga

import br.com.fenix.readerserver.dto.DtoBase
import br.com.fenix.readerserver.dto.comicinfo.ComicInfoDto
import br.com.fenix.readerserver.views.Views
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonView
import java.time.LocalDateTime
import java.util.*

data class MangaDto(
    @JsonView(Views.Summary::class)
    private var id: UUID? = null,

    @JsonView(Views.Summary::class)
    var nome: String? = null,

    @JsonView(Views.Detail::class)
    var fileName: String? = null,

    @JsonView(Views.Detail::class)
    var extension: String? = null,

    @JsonView(Views.Detail::class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var fileDate: LocalDateTime? = null,

    @JsonView(Views.Detail::class)
    var comicInfoId: UUID? = null,

    @JsonView(Views.Detail::class)
    var comicInfo: ComicInfoDto? = null
) : DtoBase<UUID?>() {

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }
}
