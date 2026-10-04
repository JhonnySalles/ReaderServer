package br.com.fenix.readerserver.repository.opf

import br.com.fenix.readerserver.model.opf.Opf
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

import org.springframework.data.jpa.repository.JpaSpecificationExecutor

@Repository
interface OpfRepository : JpaRepository<Opf, UUID>, JpaSpecificationExecutor<Opf> {

    @Query("SELECT o FROM Opf o WHERE o.title LIKE CONCAT('%', :title, '%')")
    fun findByTitleContaining(@Param("title") title: String, pageable: Pageable): Page<Opf>

    @Query("SELECT o FROM Opf o WHERE o.creator LIKE CONCAT('%', :creator, '%')")
    fun findByCreatorContaining(@Param("creator") creator: String, pageable: Pageable): Page<Opf>

    @Query("SELECT o FROM Opf o WHERE o.series LIKE CONCAT('%', :series, '%')")
    fun findBySeriesContaining(@Param("series") series: String, pageable: Pageable): Page<Opf>

    @Query("SELECT o FROM Opf o WHERE LOWER(o.title) = LOWER(:title)")
    fun findByTitleIgnoreCase(@Param("title") title: String): List<Opf>

    @Query("SELECT o FROM Opf o WHERE LOWER(o.series) = LOWER(:series) AND o.volume = :volume AND LOWER(o.language) = LOWER(:language)")
    fun findBySeriesAndVolumeAndLanguage(@Param("series") series: String, @Param("volume") volume: Float, @Param("language") language: String): List<Opf>
}
