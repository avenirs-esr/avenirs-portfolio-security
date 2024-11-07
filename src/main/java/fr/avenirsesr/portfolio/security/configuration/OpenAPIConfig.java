package fr.avenirsesr.portfolio.security.configuration;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * <h1>OpenAPIConfig</h1>
 * <p>
 * Description: OpenAPIConfig is used to configure swagger
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 07/11/2024
 */
@Configuration
public class OpenAPIConfig {

    @Value("${server.url}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url(serverUrl))
              .info(new Info()
                        .title("Avenirs Portfolio Security - API Documentation")
                        .version("v1")
                        .description("API Documentation for Avenirs Portfolio Security"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(Type.APIKEY)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(In.HEADER)
                                        .name("x-authorization")));
    }
}

