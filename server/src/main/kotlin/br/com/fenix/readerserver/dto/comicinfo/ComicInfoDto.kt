package br.com.fenix.readerserver.dto.comicinfo

import br.com.fenix.readerserver.dto.DtoBase
import br.com.fenix.readerserver.enums.comicinfo.AgeRating
import br.com.fenix.readerserver.enums.comicinfo.Manga
import br.com.fenix.readerserver.enums.comicinfo.YesNo
import br.com.fenix.readerserver.views.Views
import com.fasterxml.jackson.annotation.JsonView
import java.util.*

data class ComicInfoDto(
    @JsonView(Views.Summary::class)
    private var id: UUID? = null,
    @JsonView(Views.Detail::class)
    var idMal: Long? = null,
    @JsonView(Views.Detail::class)
    var comic: String = "",
    @JsonView(Views.Detail::class)
    var title: String = "",
    @JsonView(Views.Detail::class)
    var series: String = "",
    @JsonView(Views.Detail::class)
    var number: Float = 0f,
    @JsonView(Views.Detail::class)
    var volume: Int = 0,
    @JsonView(Views.Detail::class)
    var notes: String? = null,
    @JsonView(Views.Detail::class)
    var year: Int? = null,
    @JsonView(Views.Detail::class)
    var month: Int? = null,
    @JsonView(Views.Detail::class)
    var day: Int? = null,
    @JsonView(Views.Detail::class)
    var writer: String? = null,
    @JsonView(Views.Detail::class)
    var penciller: String? = null,
    @JsonView(Views.Detail::class)
    var inker: String? = null,
    @JsonView(Views.Detail::class)
    var coverArtist: String? = null,
    @JsonView(Views.Detail::class)
    var colorist: String? = null,
    @JsonView(Views.Detail::class)
    var letterer: String? = null,
    @JsonView(Views.Detail::class)
    var publisher: String? = null,
    @JsonView(Views.Detail::class)
    var tags: String? = null,
    @JsonView(Views.Detail::class)
    var web: String? = null,
    @JsonView(Views.Detail::class)
    var editor: String? = null,
    @JsonView(Views.Detail::class)
    var translator: String? = null,
    @JsonView(Views.Detail::class)
    var pageCount: Int? = null,
    @JsonView(Views.Detail::class)
    var pages: List<PagesDto>? = null,
    @JsonView(Views.Detail::class)
    var count: Int? = null,
    @JsonView(Views.Detail::class)
    var alternateSeries: String? = null,
    @JsonView(Views.Detail::class)
    var alternateNumber: Float? = null,
    @JsonView(Views.Detail::class)
    var storyArc: String? = null,
    @JsonView(Views.Detail::class)
    var storyArcNumber: String? = null,
    @JsonView(Views.Detail::class)
    var seriesGroup: String? = null,
    @JsonView(Views.Detail::class)
    var alternateCount: Int? = null,
    @JsonView(Views.Detail::class)
    var summary: String? = null,
    @JsonView(Views.Detail::class)
    var imprint: String? = null,
    @JsonView(Views.Detail::class)
    var genre: String? = null,
    @JsonView(Views.Detail::class)
    var languageISO: String = "",
    @JsonView(Views.Detail::class)
    var format: String? = null,
    @JsonView(Views.Detail::class)
    var ageRating: AgeRating? = null,
    @JsonView(Views.Detail::class)
    var communityRating: Float? = null,
    @JsonView(Views.Detail::class)
    var blackAndWhite: YesNo? = null,
    @JsonView(Views.Detail::class)
    var manga: Manga = Manga.Yes,
    @JsonView(Views.Detail::class)
    var characters: String? = null,
    @JsonView(Views.Detail::class)
    var teams: String? = null,
    @JsonView(Views.Detail::class)
    var locations: String? = null,
    @JsonView(Views.Detail::class)
    var scanInformation: String? = null,
    @JsonView(Views.Detail::class)
    var mainCharacterOrTeam: String? = null,
    @JsonView(Views.Detail::class)
    var review: String? = null,
) : DtoBase<UUID?>() {

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }
}
