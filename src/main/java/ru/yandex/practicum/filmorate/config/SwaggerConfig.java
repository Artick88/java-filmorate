package ru.yandex.practicum.filmorate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI swaggerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Filmorate service")
                .version("1.0.0")
                .description("Сервис по работе с рейтингом фильмов")
                .contact(new Contact()
                        .name("Patrakov Artem")));
    }
}
