package br.com.fenix.readerserver.model

interface EntityFactory<ID, E> {
    fun create(id: ID): E
}
