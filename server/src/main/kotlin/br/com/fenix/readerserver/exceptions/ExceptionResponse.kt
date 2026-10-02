package br.com.fenix.readerserver.exceptions

import java.io.Serializable
import java.time.LocalDateTime

class ExceptionResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
) : Serializable
