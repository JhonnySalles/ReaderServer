package br.com.fenix.readerserver.repository.book

import br.com.fenix.readerserver.dto.book.BookSearchFilterDto
import br.com.fenix.readerserver.model.book.Book
import br.com.fenix.readerserver.model.opf.Opf
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification

object BookSpecification {

    fun withFilters(filter: BookSearchFilterDto): Specification<Book> {
        return Specification { root, query, cb ->
            if (query.resultType != Long::class.java && query.resultType != java.lang.Long::class.java) {
                root.fetch<Book, Opf>("opf", JoinType.LEFT)
                query.distinct(true)
            }

            val predicates = mutableListOf<Predicate>()
            val opfJoin: Join<Book, Opf> = root.join("opf", JoinType.LEFT)

            // Text query
            if (!filter.query.isNullOrBlank()) {
                val pattern = "%${filter.query.trim().lowercase()}%"
                val namePred = cb.like(cb.lower(root.get("nome")), pattern)
                val filePred = cb.like(cb.lower(root.get("fileName")), pattern)
                val seriePred = cb.like(cb.lower(root.get("serie")), pattern)
                val opfTitlePred = cb.like(cb.lower(opfJoin.get("title")), pattern)
                val opfCreatorPred = cb.like(cb.lower(opfJoin.get("creator")), pattern)
                predicates.add(cb.or(namePred, filePred, seriePred, opfTitlePred, opfCreatorPred))
            }

            // Series / Serie
            val serieVal = filter.serie ?: filter.series
            if (!serieVal.isNullOrBlank()) {
                val pattern = "%${serieVal.trim().lowercase()}%"
                val bSerie = cb.like(cb.lower(root.get("serie")), pattern)
                val opfSerie = cb.like(cb.lower(opfJoin.get("series")), pattern)
                predicates.add(cb.or(bSerie, opfSerie))
            }

            // Volume
            if (!filter.volume.isNullOrBlank()) {
                val volStr = filter.volume.trim()
                val volFloat = volStr.toFloatOrNull()
                if (volFloat != null) {
                    val bVol = cb.equal(root.get<Float>("volume"), volFloat)
                    val oVol = cb.equal(opfJoin.get<Float>("volume"), volFloat)
                    predicates.add(cb.or(bVol, oVol))
                } else {
                    predicates.add(cb.like(cb.lower(root.get<String>("nome")), "%${volStr.lowercase()}%"))
                }
            }

            // Arquivo / FileName / Novel
            val arquivoVal = filter.arquivo ?: filter.fileName
            if (!arquivoVal.isNullOrBlank()) {
                val pattern = "%${arquivoVal.trim().lowercase()}%"
                val bFile = cb.like(cb.lower(root.get("fileName")), pattern)
                val oNovel = cb.like(cb.lower(opfJoin.get("novel")), pattern)
                predicates.add(cb.or(bFile, oNovel))
            }

            // Creator / Author
            val authorVal = filter.creator ?: filter.author
            if (!authorVal.isNullOrBlank()) {
                val pattern = "%${authorVal.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(opfJoin.get("creator")), pattern))
            }

            // Publisher
            if (!filter.publisher.isNullOrBlank()) {
                val pattern = "%${filter.publisher.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(opfJoin.get("publisher")), pattern))
            }

            // Language
            if (!filter.language.isNullOrBlank()) {
                val pattern = "%${filter.language.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(opfJoin.get("language")), pattern))
            }

            // Subjects
            if (!filter.subjects.isNullOrBlank()) {
                val pattern = "%${filter.subjects.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(opfJoin.get("subjects")), pattern))
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
