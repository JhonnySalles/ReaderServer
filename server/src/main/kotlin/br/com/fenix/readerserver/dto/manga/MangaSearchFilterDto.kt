package br.com.fenix.readerserver.dto.manga

data class MangaSearchFilterDto(
    val query: String? = null,
    val serie: String? = null,
    val series: String? = null,
    val volume: String? = null,
    val arquivo: String? = null,
    val fileName: String? = null,
    val author: String? = null,
    val publisher: String? = null,
    val genre: String? = null,
    val type: String? = null
)
