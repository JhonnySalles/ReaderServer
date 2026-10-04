package br.com.fenix.readerserver.dto.comicinfo

data class ComicInfoSearchFilterDto(
    val query: String? = null,
    val series: String? = null,
    val title: String? = null,
    val volume: String? = null,
    val comic: String? = null,
    val arquivo: String? = null,
    val publisher: String? = null,
    val writer: String? = null,
    val author: String? = null,
    val genre: String? = null,
    val storyArc: String? = null,
    val seriesGroup: String? = null
)
