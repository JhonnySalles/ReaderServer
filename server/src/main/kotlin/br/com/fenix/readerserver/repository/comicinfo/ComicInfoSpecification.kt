package br.com.fenix.readerserver.repository.comicinfo

import br.com.fenix.readerserver.dto.comicinfo.ComicInfoSearchFilterDto
import br.com.fenix.readerserver.model.comicinfo.ComicInfo
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification

object ComicInfoSpecification {

    fun withFilters(filter: ComicInfoSearchFilterDto): Specification<ComicInfo> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            // Free query on title, series, publisher, comic
            if (!filter.query.isNullOrBlank()) {
                val pattern = "%${filter.query.trim().lowercase()}%"
                val titlePred = cb.like(cb.lower(root.get("title")), pattern)
                val seriesPred = cb.like(cb.lower(root.get("series")), pattern)
                val comicPred = cb.like(cb.lower(root.get("comic")), pattern)
                val publisherPred = cb.like(cb.lower(root.get("publisher")), pattern)
                predicates.add(cb.or(titlePred, seriesPred, comicPred, publisherPred))
            }

            // Series
            if (!filter.series.isNullOrBlank()) {
                val pattern = "%${filter.series.trim().lowercase()}%"
                val seriesPred = cb.like(cb.lower(root.get("series")), pattern)
                val altSeriesPred = cb.like(cb.lower(root.get("alternateSeries")), pattern)
                predicates.add(cb.or(seriesPred, altSeriesPred))
            }

            // Title
            if (!filter.title.isNullOrBlank()) {
                val pattern = "%${filter.title.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("title")), pattern))
            }

            // Volume
            if (!filter.volume.isNullOrBlank()) {
                val volStr = filter.volume.trim()
                val volFloat = volStr.toFloatOrNull()
                if (volFloat != null) {
                    predicates.add(cb.equal(root.get<Float>("volume"), volFloat))
                }
            }

            // Comic / Arquivo
            val comicVal = filter.comic ?: filter.arquivo
            if (!comicVal.isNullOrBlank()) {
                val pattern = "%${comicVal.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("comic")), pattern))
            }

            // Publisher
            if (!filter.publisher.isNullOrBlank()) {
                val pattern = "%${filter.publisher.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("publisher")), pattern))
            }

            // Writer / Author
            val authorVal = filter.writer ?: filter.author
            if (!authorVal.isNullOrBlank()) {
                val pattern = "%${authorVal.trim().lowercase()}%"
                val writerPred = cb.like(cb.lower(root.get("writer")), pattern)
                predicates.add(writerPred)
            }

            // Genre
            if (!filter.genre.isNullOrBlank()) {
                val pattern = "%${filter.genre.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("genre")), pattern))
            }

            // StoryArc
            if (!filter.storyArc.isNullOrBlank()) {
                val pattern = "%${filter.storyArc.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("storyArc")), pattern))
            }

            // SeriesGroup
            if (!filter.seriesGroup.isNullOrBlank()) {
                val pattern = "%${filter.seriesGroup.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("seriesGroup")), pattern))
            }

            if (predicates.isEmpty()) cb.conjunction() else cb.and(*predicates.toTypedArray())
        }
    }
}
