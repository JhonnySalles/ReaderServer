package br.com.fenix.readerserver.repository.manga

import br.com.fenix.readerserver.model.manga.Manga
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

import org.springframework.data.jpa.repository.JpaSpecificationExecutor

@Repository
interface MangaRepository : JpaRepository<Manga, UUID?>, JpaSpecificationExecutor<Manga> {

    @EntityGraph(attributePaths = ["comicInfo"])
    @Query("SELECT m FROM Manga m WHERE m.id = :id")
    fun findByIdWithComicInfo(@Param("id") id: UUID): Optional<Manga>

    @EntityGraph(attributePaths = ["comicInfo"])
    @Query("SELECT m FROM Manga m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    fun findByNome(@Param("nome") nome: String, pageable: Pageable): Page<Manga>

    @EntityGraph(attributePaths = ["comicInfo"])
    @Query("SELECT m FROM Manga m WHERE LOWER(m.fileName) LIKE LOWER(CONCAT('%', :fileName, '%'))")
    fun findByFileName(@Param("fileName") fileName: String, pageable: Pageable): Page<Manga>

    @EntityGraph(attributePaths = ["comicInfo"])
    @Query("SELECT m FROM Manga m WHERE m.comicInfoId = :comicInfoId")
    fun findByComicInfoId(@Param("comicInfoId") comicInfoId: UUID): List<Manga>

    @EntityGraph(attributePaths = ["comicInfo"])
    @Query("SELECT m FROM Manga m WHERE m.comicInfoId = :comicInfoId")
    fun findByComicInfoIdPaged(@Param("comicInfoId") comicInfoId: UUID, pageable: Pageable): Page<Manga>
}
