package br.com.fenix.readerserver.dto.book

data class BookSearchFilterDto(
    val query: String? = null,
    val serie: String? = null,
    val series: String? = null,
    val volume: String? = null,
    val arquivo: String? = null,
    val fileName: String? = null,
    val creator: String? = null,
    val author: String? = null,
    val publisher: String? = null,
    val language: String? = null,
    val subjects: String? = null,
    val type: String? = null
)
