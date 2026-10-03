package br.com.fenix.readerserver.dto.opf

import br.com.fenix.readerserver.dto.DtoBase
import br.com.fenix.readerserver.views.Views
import com.fasterxml.jackson.annotation.JsonView
import java.util.*

data class OpfDto(
    @JsonView(Views.Summary::class)
    private var id: UUID? = null,

    @JsonView(Views.Detail::class)
    var title: String = "",

    @JsonView(Views.Detail::class)
    var creator: String? = null,

    @JsonView(Views.Detail::class)
    var contributor: String? = null,

    @JsonView(Views.Detail::class)
    var publisher: String? = null,

    @JsonView(Views.Detail::class)
    var datePublished: String? = null,

    @JsonView(Views.Detail::class)
    var description: String? = null,

    @JsonView(Views.Detail::class)
    var subjects: String? = null,

    @JsonView(Views.Detail::class)
    var language: String = "en",

    @JsonView(Views.Detail::class)
    var identifiers: String? = null,

    @JsonView(Views.Detail::class)
    var series: String? = null,

    @JsonView(Views.Detail::class)
    var seriesIndex: String? = null,

    @JsonView(Views.Detail::class)
    var rights: String? = null,

    @JsonView(Views.Detail::class)
    var relation: String? = null
) : DtoBase<UUID?>() {

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }
}
