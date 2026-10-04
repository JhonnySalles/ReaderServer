package br.com.fenix.readerserver.repository.opf

import br.com.fenix.readerserver.dto.opf.OpfSearchFilterDto
import br.com.fenix.readerserver.model.opf.Opf
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification

object OpfSpecification {

    fun withFilters(filter: OpfSearchFilterDto): Specification<Opf> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            // Free query
            if (!filter.query.isNullOrBlank()) {
                val pattern = "%${filter.query.trim().lowercase()}%"
                val titlePred = cb.like(cb.lower(root.get("title")), pattern)
                val creatorPred = cb.like(cb.lower(root.get("creator")), pattern)
                val seriesPred = cb.like(cb.lower(root.get("series")), pattern)
                val publisherPred = cb.like(cb.lower(root.get("publisher")), pattern)
                val subjectsPred = cb.like(cb.lower(root.get("subjects")), pattern)
                predicates.add(cb.or(titlePred, creatorPred, seriesPred, publisherPred, subjectsPred))
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

            // Novel / Arquivo
            val novelVal = filter.novel ?: filter.arquivo
            if (!novelVal.isNullOrBlank()) {
                val pattern = "%${novelVal.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("novel")), pattern))
            }

            // Creator / Author
            val authorVal = filter.creator ?: filter.author
            if (!authorVal.isNullOrBlank()) {
                val pattern = "%${authorVal.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("creator")), pattern))
            }

            // Series
            if (!filter.series.isNullOrBlank()) {
                val pattern = "%${filter.series.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("series")), pattern))
            }

            // Publisher
            if (!filter.publisher.isNullOrBlank()) {
                val pattern = "%${filter.publisher.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("publisher")), pattern))
            }

            // Language
            if (!filter.language.isNullOrBlank()) {
                val pattern = "%${filter.language.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("language")), pattern))
            }

            // Subjects
            if (!filter.subjects.isNullOrBlank()) {
                val pattern = "%${filter.subjects.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("subjects")), pattern))
            }

            if (predicates.isEmpty()) cb.conjunction() else cb.and(*predicates.toTypedArray())
        }
    }
}
