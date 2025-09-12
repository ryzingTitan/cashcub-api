package com.ryzingtitan.cashcub.presentation.configuration

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration(
    private val buildProperties: BuildProperties,
) {
    @Bean
    fun customOpenAPI(): OpenAPI {
        val scopes = Scopes()
        scopes["openid"] = "openid"
        scopes["email"] = "email"
        scopes["profile"] = "profile"

        val extensions = mutableMapOf<String, Any>("x-tokenName" to "id_token")

        return OpenAPI()
            .components(
                Components().addSecuritySchemes(
                    "jwt",
                    SecurityScheme().type(SecurityScheme.Type.OAUTH2).extensions(extensions).flows(
                        OAuthFlows().authorizationCode(
                            OAuthFlow()
                                .authorizationUrl("https://accounts.google.com/o/oauth2/v2/auth")
                                .tokenUrl("https://oauth2.googleapis.com/token")
                                .scopes(scopes),
                        ),
                    ),
                ),
            ).info(
                Info()
                    .title("CashCub API")
                    .description("Swagger spec for the CashCub API project")
                    .version(buildProperties.version),
            )
    }
}
