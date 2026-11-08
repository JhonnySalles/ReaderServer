package br.com.fenix.readerserver.model.auth

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "seguranca")
data class Seguranca(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "api_key", nullable = false, columnDefinition = "TEXT")
    var apiKey: String = "",

    @Column(name = "descricao")
    var descricao: String? = null,

    @Column(name = "ativo", nullable = false)
    var ativo: Boolean = true,

    @Column(name = "criado_em")
    var criadoEm: LocalDateTime? = LocalDateTime.now()
) : Serializable

