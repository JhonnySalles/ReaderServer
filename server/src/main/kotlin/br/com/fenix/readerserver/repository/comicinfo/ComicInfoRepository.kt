package br.com.fenix.readerserver.repository.comicinfo

import br.com.fenix.readerserver.model.comicinfo.ComicInfo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

import org.springframework.data.jpa.repository.JpaSpecificationExecutor

@Repository
interface ComicInfoRepository : JpaRepository<ComicInfo, UUID>, JpaSpecificationExecutor<ComicInfo> {

    @Query("SELECT c FROM ComicInfo c WHERE c.series LIKE CONCAT('%', :series, '%')")
    fun findBySeriesContaining(@Param("series") series: String, pageable: Pageable): Page<ComicInfo>

    @Query("SELECT c FROM ComicInfo c WHERE c.title LIKE CONCAT('%', :title, '%')")
    fun findByTitleContaining(@Param("title") title: String, pageable: Pageable): Page<ComicInfo>
}
