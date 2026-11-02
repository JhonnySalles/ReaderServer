package br.com.fenix.readerserver.config

import br.com.fenix.readerserver.converters.MediaTypes
import br.com.fenix.readerserver.converters.YamlJackson2HttpMesageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    @Value("\${cors.originPatterns:http://localhost:3000,http://localhost:8080,http://localhost:5173,http://localhost:5174,http://localhost:5179}")
    private val corsOriginPatterns: String
) : WebMvcConfigurer {

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(YamlJackson2HttpMesageConverter())
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        val allowedOrigins = corsOriginPatterns
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toTypedArray()

        registry.addMapping("/**")
            .allowedMethods("*")
            .allowedOriginPatterns(*allowedOrigins)
            .exposedHeaders("Content-Disposition")
            .allowCredentials(true)
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.favorParameter(false)
            .ignoreAcceptHeader(false)
            .useRegisteredExtensionsOnly(false)
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML)
            .mediaType("x-yaml", MediaTypes.MEDIA_TYPE_APPLICATION_YML)
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addRedirectViewController("/docs", "/swagger-ui/index.html")
        registry.addRedirectViewController("/documentacao", "/swagger-ui/index.html")
    }
}
