package br.com.fenix.readerserver.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.Links
import org.springframework.hateoas.RepresentationModel
import java.io.Serializable

abstract class DtoBase<ID>() : RepresentationModel<DtoBase<ID>>(), Dto<ID>, Serializable {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    override fun getLinks(): Links {
        return super.getLinks()
    }
}
