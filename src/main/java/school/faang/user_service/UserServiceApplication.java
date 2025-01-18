package school.faang.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import school.faang.user_service.config.EnvHelper;

@SpringBootApplication
@EnableFeignClients("school.faang.user_service.client")
public class UserServiceApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        new EnvHelper().loadVariables();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}