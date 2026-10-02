package br.com.fenix.readerserver.dto

interface Dto<ID> {
    fun getId(): ID
    fun setId(id: ID)
}
