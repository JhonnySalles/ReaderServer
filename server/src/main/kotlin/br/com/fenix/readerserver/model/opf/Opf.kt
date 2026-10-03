package br.com.fenix.readerserver.model.opf

import br.com.fenix.readerserver.model.EntityBase
import br.com.fenix.readerserver.model.EntityFactory
import jakarta.persistence.*
import jakarta.xml.bind.annotation.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "opf")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "package", namespace = "http://www.idpf.org/2007/opf")
data class Opf(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    @field:XmlElement(name = "id")
    private var id: UUID? = null,

    @Column(name = "title", length = 900, nullable = true)
    @field:XmlElement(name = "title")
    var title: String = "",

    @Column(name = "creator", length = 900, nullable = true)
    @field:XmlElement(name = "creator")
    var creator: String? = null,

    @Column(name = "contributor", length = 900, nullable = true)
    @field:XmlElement(name = "contributor")
    var contributor: String? = null,

    @Column(name = "publisher", length = 300, nullable = true)
    @field:XmlElement(name = "publisher")
    var publisher: String? = null,

    @Column(name = "date_published", length = 100, nullable = true)
    @field:XmlElement(name = "date")
    var datePublished: String? = null,

    @Column(name = "description", length = 10485760, nullable = true)
    @field:XmlElement(name = "description")
    var description: String? = null,

    @Column(name = "subjects", length = 900, nullable = true)
    @field:XmlElement(name = "subject")
    var subjects: String? = null,

    @Column(name = "language", length = 10, nullable = true)
    @field:XmlElement(name = "language")
    var language: String = "en",

    @Column(name = "identifiers", length = 900, nullable = true)
    @field:XmlElement(name = "identifier")
    var identifiers: String? = null,

    @Column(name = "series", length = 900, nullable = true)
    @field:XmlElement(name = "series")
    var series: String? = null,

    @Column(name = "series_index", length = 50, nullable = true)
    @field:XmlElement(name = "seriesIndex")
    var seriesIndex: String? = null,

    @Column(name = "rights", length = 500, nullable = true)
    @field:XmlElement(name = "rights")
    var rights: String? = null,

    @Column(name = "relation", length = 500, nullable = true)
    @field:XmlElement(name = "relation")
    var relation: String? = null
) : Serializable, EntityBase<UUID?, Opf>() {

    companion object : EntityFactory<UUID?, Opf> {
        override fun create(id: UUID?): Opf = Opf(
            id = id,
            title = "",
            creator = null,
            contributor = null,
            publisher = null,
            datePublished = null,
            description = null,
            subjects = null,
            language = "en",
            identifiers = null,
            series = null,
            seriesIndex = null,
            rights = null,
            relation = null
        )
    }

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }

    override fun merge(source: Opf) {
        this.id = source.id
        this.title = source.title
        this.creator = source.creator
        this.contributor = source.contributor
        this.publisher = source.publisher
        this.datePublished = source.datePublished
        this.description = source.description
        this.subjects = source.subjects
        this.language = source.language
        this.identifiers = source.identifiers
        this.series = source.series
        this.seriesIndex = source.seriesIndex
        this.rights = source.rights
        this.relation = source.relation
    }

    override fun patch(source: Opf) {
        if (source.title.isNotEmpty())
            this.title = source.title
        if (source.creator != null)
            this.creator = source.creator
        if (source.contributor != null)
            this.contributor = source.contributor
        if (source.publisher != null)
            this.publisher = source.publisher
        if (source.datePublished != null)
            this.datePublished = source.datePublished
        if (source.description != null)
            this.description = source.description
        if (source.subjects != null)
            this.subjects = source.subjects
        if (source.language.isNotEmpty())
            this.language = source.language
        if (source.identifiers != null)
            this.identifiers = source.identifiers
        if (source.series != null)
            this.series = source.series
        if (source.seriesIndex != null)
            this.seriesIndex = source.seriesIndex
        if (source.rights != null)
            this.rights = source.rights
        if (source.relation != null)
            this.relation = source.relation
    }
}
