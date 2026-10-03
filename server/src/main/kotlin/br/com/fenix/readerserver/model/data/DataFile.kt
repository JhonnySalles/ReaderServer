package br.com.fenix.readerserver.model.data

import br.com.fenix.readerserver.model.EntityBase
import br.com.fenix.readerserver.model.EntityFactory
import jakarta.persistence.*
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DataFile")
data class DataFile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    @field:XmlElement(name = "id")
    private var id: UUID? = null,

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "comicinfo_id", length = 36, nullable = true)
    @field:XmlElement(name = "comicInfoId")
    var comicInfoId: UUID? = null,

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "opf_id", length = 36, nullable = true)
    @field:XmlElement(name = "opfId")
    var opfId: UUID? = null,

    @Column(name = "tipo", length = 50, nullable = true)
    @field:XmlElement(name = "tipo")
    var tipo: String? = null,

    @Column(name = "file_name", length = 255, nullable = true)
    @field:XmlElement(name = "fileName")
    var fileName: String? = null,

    @Column(name = "file_content", length = 10485760, nullable = true)
    @field:XmlElement(name = "fileContent")
    var fileContent: String? = null
) : Serializable, EntityBase<UUID?, DataFile>() {

    companion object : EntityFactory<UUID?, DataFile> {
        override fun create(id: UUID?): DataFile = DataFile(
            id = id,
            comicInfoId = null,
            opfId = null,
            tipo = null,
            fileName = null,
            fileContent = null
        )
    }

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }

    override fun merge(source: DataFile) {
        this.id = source.id
        this.comicInfoId = source.comicInfoId
        this.opfId = source.opfId
        this.tipo = source.tipo
        this.fileName = source.fileName
        this.fileContent = source.fileContent
    }

    override fun patch(source: DataFile) {
        if (source.comicInfoId != null)
            this.comicInfoId = source.comicInfoId
        if (source.opfId != null)
            this.opfId = source.opfId
        if (source.tipo != null)
            this.tipo = source.tipo
        if (source.fileName != null)
            this.fileName = source.fileName
        if (source.fileContent != null)
            this.fileContent = source.fileContent
    }
}
