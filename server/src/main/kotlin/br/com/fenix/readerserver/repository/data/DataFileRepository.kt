package br.com.fenix.readerserver.repository.data

import br.com.fenix.readerserver.model.data.DataFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DataFileRepository : JpaRepository<DataFile, UUID> {

    fun findByComicInfoId(comicInfoId: UUID): List<DataFile>

    fun findByOpfId(opfId: UUID): List<DataFile>

    @Query("SELECT d FROM DataFile d WHERE d.comicInfoId = :comicInfoId")
    fun findByComicInfoIdPaged(@Param("comicInfoId") comicInfoId: UUID, pageable: Pageable): Page<DataFile>

    @Query("SELECT d FROM DataFile d WHERE d.opfId = :opfId")
    fun findByOpfIdPaged(@Param("opfId") opfId: UUID, pageable: Pageable): Page<DataFile>
}
