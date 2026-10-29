package pe.edu.upeu.autoandes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("bearerAuth",
                        new io.swagger.v3.oas.models.security.SecurityScheme().type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth"))
                .info(new Info()
                        .title("AutoAndes API REST")
                        .version("v1.0")
                        .description("Documentación interactiva de la API para el taller mecánico AutoAndes"));
    }
}
