package br.com.fenix.readerserver.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class InvalidNotFoundException(message: String) : RuntimeException(message)
