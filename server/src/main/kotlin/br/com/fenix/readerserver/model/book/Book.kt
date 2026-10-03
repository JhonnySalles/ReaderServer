package br.com.fenix.readerserver.model.book

import br.com.fenix.readerserver.model.EntityBase
import br.com.fenix.readerserver.model.EntityFactory
import br.com.fenix.readerserver.model.opf.Opf
import jakarta.persistence.*
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "book")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Book")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    @field:XmlElement(name = "id")
    private var id: UUID? = null,

    @Column(name = "nome", length = 500, nullable = true)
    @field:XmlElement(name = "nome")
    var nome: String? = null,

    @Column(name = "file_name", length = 500, nullable = true)
    @field:XmlElement(name = "fileName")
    var fileName: String? = null,

    @Column(name = "extension", length = 10, nullable = true)
    @field:XmlElement(name = "extension")
    var extension: String? = null,

    @Column(name = "file_date", nullable = true)
    @field:XmlElement(name = "fileDate")
    var fileDate: LocalDateTime? = null,

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "opf_id", length = 36, nullable = true)
    @field:XmlElement(name = "opfId")
    var opfId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opf_id", insertable = false, updatable = false)
    var opf: Opf? = null
) : Serializable, EntityBase<UUID?, Book>() {

    companion object : EntityFactory<UUID?, Book> {
        override fun create(id: UUID?): Book = Book(
            id = id,
            nome = null,
            fileName = null,
            extension = null,
            fileDate = null,
            opfId = null,
            opf = null
        )
    }

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }

    override fun merge(source: Book) {
        this.id = source.id
        this.nome = source.nome
        this.fileName = source.fileName
        this.extension = source.extension
        this.fileDate = source.fileDate
        this.opfId = source.opfId
    }

    override fun patch(source: Book) {
        if (source.nome != null)
            this.nome = source.nome
        if (source.fileName != null)
            this.fileName = source.fileName
        if (source.extension != null)
            this.extension = source.extension
        if (source.fileDate != null)
            this.fileDate = source.fileDate
        if (source.opfId != null)
            this.opfId = source.opfId
    }
}
