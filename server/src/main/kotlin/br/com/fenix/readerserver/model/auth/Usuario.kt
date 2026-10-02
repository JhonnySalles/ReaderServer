package br.com.fenix.readerserver.model.auth

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable

@Entity
@Table(name = "usuarios")
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(unique = true, nullable = false)
    private val username: String,
    @Column(nullable = false)
    val nome: String,
    @Column(nullable = false)
    private val password: String,
    @Column(name = "conta_nao_expirada", nullable = false)
    val contaNaoExpirada: Boolean = true,
    @Column(name = "conta_nao_travada", nullable = false)
    val contaNaoTravada: Boolean = true,
    @Column(name = "credencial_nao_expirado", nullable = false)
    val credencialNaoExpirada: Boolean = true,
    @Column(name = "ativo", nullable = false)
    val ativo: Boolean = true,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuarios_permissoes",
        joinColumns = [JoinColumn(name = "id_usuario")],
        inverseJoinColumns = [JoinColumn(name = "id_permissao")]
    )
    val permissoes: MutableList<Permissao> = mutableListOf(),
) : UserDetails, Serializable {

    fun getRoles(): List<String> {
        return permissoes.map { it.descricao }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return permissoes
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return contaNaoExpirada
    }

    override fun isAccountNonLocked(): Boolean {
        return contaNaoTravada
    }

    override fun isCredentialsNonExpired(): Boolean {
        return credencialNaoExpirada
    }

    override fun isEnabled(): Boolean {
        return ativo
    }
}
