package school.faang.user_service.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Микросервис пользователей",
        version = "1.0",
        description = "Микросервис для работы с пользователями, их событиями, а также продвижением"
))
public class SwaggerConfig {
}

