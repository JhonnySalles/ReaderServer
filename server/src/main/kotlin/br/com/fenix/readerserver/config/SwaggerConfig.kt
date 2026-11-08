package br.com.fenix.readerserver.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod

@Configuration
class SwaggerConfig {

    companion object {
        const val BEARER_AUTH = "BearerAuth"
        const val API_KEY_AUTH = "ApiKeyAuth"
    }

    @Bean
    fun customOpenApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("ReaderServer API")
                    .version("v1")
                    .description(
                        """
                        API REST para biblioteca de mangás, livros/ePubs, ComicInfo e metadados OPF.
                        
                        ### 🔐 Requisitos de Segurança:
                        - **API Key (`X-API-KEY`)**: Obrigatória em todas as requisições de serviço (incluindo autenticação em `/auth/signin` e endpoints de negócio `/api/**`). A chave deve estar cadastrada e ativa (`ativo = true`) na tabela `seguranca`.
                        - **Token JWT (`Bearer Token`)**: Obrigatório para endpoints protegidos de negócio (`/api/**`), obtido através do login em `/auth/signin`.
                        
                        💡 *Para testar requisições pelo Swagger UI, utilize o botão **Authorize** informando a `X-API-KEY` no campo **ApiKeyAuth** e o Token JWT no campo **BearerAuth**.*
                        """.trimIndent()
                    )
                    .termsOfService("")
                    .license(License().name("Apache 2.0").url("https://springdoc.org"))
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        API_KEY_AUTH,
                        SecurityScheme()
                            .name("X-API-KEY")
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .description("Chave de segurança da API cadastrada na tabela 'seguranca'. Cabeçalho HTTP: 'X-API-KEY'. Obrigatória em todas as chamadas.")
                    )
                    .addSecuritySchemes(
                        BEARER_AUTH,
                        SecurityScheme()
                            .name(BEARER_AUTH)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Token de acesso JWT obtido através de POST /auth/signin.")
                    )
            )
    }

    @Bean
    fun customizeOperations(): OperationCustomizer {
        return OperationCustomizer { operation: Operation, handlerMethod: HandlerMethod ->
            val className = handlerMethod.beanType.simpleName

            // Endpoint de Health check não exige credenciais
            if (className.contains("HealthCheck", ignoreCase = true)) {
                operation.security = emptyList()
                return@OperationCustomizer operation
            }

            // Endpoints de autenticação (/auth/**) exigem apenas a API Key
            if (className.contains("Auth", ignoreCase = true) || handlerMethod.method.declaringClass.name.contains(".auth.")) {
                operation.security = listOf(
                    SecurityRequirement().addList(API_KEY_AUTH)
                )
                if (operation.responses == null) {
                    operation.responses = io.swagger.v3.oas.models.responses.ApiResponses()
                }
                operation.responses.addApiResponse(
                    "401",
                    ApiResponse().description("Não autorizado: API Key ausente, inativa ou inválida no cabeçalho X-API-KEY.")
                )
                return@OperationCustomizer operation
            }

            // Todos os demais endpoints de negócio (/api/**) exigem API Key + Bearer Token JWT
            operation.security = listOf(
                SecurityRequirement()
                    .addList(API_KEY_AUTH)
                    .addList(BEARER_AUTH)
            )
            if (operation.responses == null) {
                operation.responses = io.swagger.v3.oas.models.responses.ApiResponses()
            }
            operation.responses.addApiResponse(
                "401",
                ApiResponse().description("Não autorizado: API Key ausente/inválida ou Token JWT ausente/expirado.")
            )
            operation.responses.addApiResponse(
                "403",
                ApiResponse().description("Acesso proibido: Credenciais insuficientes.")
            )

            operation
        }
    }
}
