package br.com.fenix.readerserver.dto.auth

import java.io.Serializable

data class CredencialDto(
    val username: String?,
    val password: String?
) : Serializable {
    constructor() : this("", "")
}
