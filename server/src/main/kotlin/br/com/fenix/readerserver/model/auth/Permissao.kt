package br.com.fenix.readerserver.model.auth

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import java.io.Serializable

@Entity
@Table(name = "permissoes")
data class Permissao(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    val descricao: String
) : GrantedAuthority, Serializable {

    override fun getAuthority(): String {
        return descricao
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Permissao
        if (id != other.id) return false
        if (descricao != other.descricao) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + descricao.hashCode()
        return result
    }
}
