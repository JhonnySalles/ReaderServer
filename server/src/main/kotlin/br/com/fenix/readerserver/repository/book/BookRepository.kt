package br.com.fenix.readerserver.repository.book

import br.com.fenix.readerserver.model.book.Book
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BookRepository : JpaRepository<Book, UUID?> {

    @EntityGraph(attributePaths = ["opf"])
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    fun findByIdWithOpf(@Param("id") id: UUID): Optional<Book>

    @EntityGraph(attributePaths = ["opf"])
    @Query("SELECT b FROM Book b WHERE LOWER(b.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    fun findByNome(@Param("nome") nome: String, pageable: Pageable): Page<Book>

    @EntityGraph(attributePaths = ["opf"])
    @Query("SELECT b FROM Book b WHERE LOWER(b.fileName) LIKE LOWER(CONCAT('%', :fileName, '%'))")
    fun findByFileName(@Param("fileName") fileName: String, pageable: Pageable): Page<Book>

    @EntityGraph(attributePaths = ["opf"])
    @Query("SELECT b FROM Book b WHERE b.opfId = :opfId")
    fun findByOpfId(@Param("opfId") opfId: UUID): List<Book>

    @EntityGraph(attributePaths = ["opf"])
    @Query("SELECT b FROM Book b WHERE b.opfId = :opfId")
    fun findByOpfIdPaged(@Param("opfId") opfId: UUID, pageable: Pageable): Page<Book>
}
