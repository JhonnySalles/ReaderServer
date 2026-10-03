package br.com.fenix.readerserver.dto.book

import br.com.fenix.readerserver.dto.DtoBase
import br.com.fenix.readerserver.dto.opf.OpfDto
import br.com.fenix.readerserver.views.Views
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonView
import java.time.LocalDateTime
import java.util.*

data class BookDto(
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
    var opfId: UUID? = null,

    @JsonView(Views.Detail::class)
    var opf: OpfDto? = null
) : DtoBase<UUID?>() {

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }
}
