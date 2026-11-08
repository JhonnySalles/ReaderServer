package br.com.fenix.readerserver.repository.auth

import br.com.fenix.readerserver.model.auth.Seguranca
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SegurancaRepository : JpaRepository<Seguranca, Long> {

    @Query("SELECT s FROM Seguranca s WHERE s.apiKey = :apiKey AND s.ativo = true")
    fun findByApiKeyAndAtivoTrue(@Param("apiKey") apiKey: String): Optional<Seguranca>

    fun existsByApiKeyAndAtivoTrue(apiKey: String): Boolean
}

