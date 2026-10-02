package br.com.fenix.readerserver.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class RequiredObjectIsNullException(message: String = "It is not allowed to persist a null object!") : RuntimeException(message)
