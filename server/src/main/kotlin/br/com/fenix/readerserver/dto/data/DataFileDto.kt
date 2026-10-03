package br.com.fenix.readerserver.dto.data

import br.com.fenix.readerserver.dto.DtoBase
import br.com.fenix.readerserver.views.Views
import com.fasterxml.jackson.annotation.JsonView
import java.util.*

data class DataFileDto(
    @JsonView(Views.Summary::class)
    private var id: UUID? = null,

    @JsonView(Views.Detail::class)
    var comicInfoId: UUID? = null,

    @JsonView(Views.Detail::class)
    var opfId: UUID? = null,

    @JsonView(Views.Detail::class)
    var tipo: String? = null,

    @JsonView(Views.Detail::class)
    var fileName: String? = null,

    @JsonView(Views.Detail::class)
    var fileContent: String? = null
) : DtoBase<UUID?>() {

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }
}
