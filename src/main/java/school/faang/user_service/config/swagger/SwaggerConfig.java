package school.faang.user_service.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Микросервис постов",
        version = "1.0",
        description = "Микросервис для работы с постами, рекламой!"
    ))
public class SwaggerConfig {
}

