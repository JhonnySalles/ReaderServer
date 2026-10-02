package br.com.fenix.readerserver.converters

import org.springframework.http.MediaType

class MediaTypes {
    companion object {
        const val MEDIA_TYPE_APPLICATION_YML_VALUE = "application/x-yaml"
        val MEDIA_TYPE_APPLICATION_YML: MediaType = MediaType.valueOf(MEDIA_TYPE_APPLICATION_YML_VALUE)
    }
}
