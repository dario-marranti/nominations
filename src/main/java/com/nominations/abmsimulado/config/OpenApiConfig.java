package com.example.abmsimulado.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI abmSimuladoOpenApi() {
        return new OpenAPI().info(new Info()
                .title("ABM SIMULADO")
                .version("v1")
                .description("""
                        Simula un servicio asincrono de nominaciones.

                        1. `POST /v1/nominations` persiste en H2 y devuelve un `uuid` (202 Accepted).
                        2. La primera vez que se consulta `GET /v1/nominations/{uuid}` se sortea una demora aleatoria de 0 a 30 s.
                        3. Mientras no se cumpla la demora, el GET responde **200** sin novedades (`PENDING`).
                        4. Cumplida la demora, el GET responde **201** con el `id` de la base de datos (`CREATED`).
                        """));
    }
}
