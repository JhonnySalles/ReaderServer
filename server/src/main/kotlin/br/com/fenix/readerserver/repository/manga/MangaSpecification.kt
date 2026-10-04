package br.com.fenix.readerserver.repository.manga

import br.com.fenix.readerserver.dto.manga.MangaSearchFilterDto
import br.com.fenix.readerserver.model.comicinfo.ComicInfo
import br.com.fenix.readerserver.model.manga.Manga
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification

object MangaSpecification {

    fun withFilters(filter: MangaSearchFilterDto): Specification<Manga> {
        return Specification { root, query, cb ->
            // EntityGraph or fetch join for comicInfo if query is not count
            if (query.resultType != Long::class.java && query.resultType != java.lang.Long::class.java) {
                root.fetch<Manga, ComicInfo>("comicInfo", JoinType.LEFT)
                query.distinct(true)
            }

            val predicates = mutableListOf<Predicate>()

            val comicInfoJoin: Join<Manga, ComicInfo> = root.join("comicInfo", JoinType.LEFT)

            // Text query (searches in manga.nome, manga.fileName, manga.serie or comicInfo.title/series)
            if (!filter.query.isNullOrBlank()) {
                val pattern = "%${filter.query.trim().lowercase()}%"
                val namePred = cb.like(cb.lower(root.get("nome")), pattern)
                val filePred = cb.like(cb.lower(root.get("fileName")), pattern)
                val seriePred = cb.like(cb.lower(root.get("serie")), pattern)
                val comicTitlePred = cb.like(cb.lower(comicInfoJoin.get("title")), pattern)
                val comicSeriesPred = cb.like(cb.lower(comicInfoJoin.get("series")), pattern)
                predicates.add(cb.or(namePred, filePred, seriePred, comicTitlePred, comicSeriesPred))
            }

            // Series / Serie
            val serieVal = filter.serie ?: filter.series
            if (!serieVal.isNullOrBlank()) {
                val pattern = "%${serieVal.trim().lowercase()}%"
                val mSerie = cb.like(cb.lower(root.get("serie")), pattern)
                val cSerie = cb.like(cb.lower(comicInfoJoin.get("series")), pattern)
                val cAltSerie = cb.like(cb.lower(comicInfoJoin.get("alternateSeries")), pattern)
                predicates.add(cb.or(mSerie, cSerie, cAltSerie))
            }

            // Volume
            if (!filter.volume.isNullOrBlank()) {
                val volStr = filter.volume.trim()
                val volFloat = volStr.toFloatOrNull()
                if (volFloat != null) {
                    val mVol = cb.equal(root.get<Float>("volume"), volFloat)
                    val cVol = cb.equal(comicInfoJoin.get<Float>("volume"), volFloat)
                    predicates.add(cb.or(mVol, cVol))
                } else {
                    // try like on string or converted
                    predicates.add(cb.like(cb.lower(root.get<String>("nome")), "%${volStr.lowercase()}%"))
                }
            }

            // Arquivo / FileName / Comic
            val arquivoVal = filter.arquivo ?: filter.fileName
            if (!arquivoVal.isNullOrBlank()) {
                val pattern = "%${arquivoVal.trim().lowercase()}%"
                val mFile = cb.like(cb.lower(root.get("fileName")), pattern)
                val cComic = cb.like(cb.lower(comicInfoJoin.get("comic")), pattern)
                predicates.add(cb.or(mFile, cComic))
            }

            // Author / Writer
            if (!filter.author.isNullOrBlank()) {
                val pattern = "%${filter.author.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(comicInfoJoin.get("writer")), pattern))
            }

            // Publisher
            if (!filter.publisher.isNullOrBlank()) {
                val pattern = "%${filter.publisher.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(comicInfoJoin.get("publisher")), pattern))
            }

            // Genre
            if (!filter.genre.isNullOrBlank()) {
                val pattern = "%${filter.genre.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(comicInfoJoin.get("genre")), pattern))
            }

            // Type / Extension
            if (!filter.type.isNullOrBlank()) {
                val pattern = "%${filter.type.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("extension")), pattern))
            }

            if (predicates.isEmpty()) cb.conjunction() else cb.and(*predicates.toTypedArray())
        }
    }
}
