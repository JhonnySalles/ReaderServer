package br.com.fenix.readerserver.dto.opf

data class OpfSearchFilterDto(
    val query: String? = null,
    val title: String? = null,
    val volume: String? = null,
    val novel: String? = null,
    val arquivo: String? = null,
    val creator: String? = null,
    val author: String? = null,
    val series: String? = null,
    val publisher: String? = null,
    val language: String? = null,
    val subjects: String? = null
)
