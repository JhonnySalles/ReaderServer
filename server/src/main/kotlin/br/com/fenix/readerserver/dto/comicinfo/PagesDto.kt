package br.com.fenix.readerserver.dto.comicinfo

import br.com.fenix.readerserver.enums.comicinfo.ComicPageType
import br.com.fenix.readerserver.views.Views
import com.fasterxml.jackson.annotation.JsonView
import java.io.Serializable

data class PagesDto(
    @JsonView(Views.Detail::class)
    var bookmark: String? = null,
    @JsonView(Views.Detail::class)
    var image: Int? = null,
    @JsonView(Views.Detail::class)
    var imageHeight: Int? = null,
    @JsonView(Views.Detail::class)
    var imageWidth: Int? = null,
    @JsonView(Views.Detail::class)
    var imageSize: Long? = null,
    @JsonView(Views.Detail::class)
    var type: ComicPageType? = null,
    @JsonView(Views.Detail::class)
    var doublePage: Boolean? = null,
    @JsonView(Views.Detail::class)
    var key: String? = null
) : Serializable
