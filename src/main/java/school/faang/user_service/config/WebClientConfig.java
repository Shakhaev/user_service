package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    // DiceBear

    @Value("${diceBear.url}")
    private String diceBearBaseUrl;
    @Value("${httpClient.diceBear.timeout}")
    private Integer diceBearTimeout;

    @Bean
    public WebClient diceBearWebClient() {
        return WebClient.builder().baseUrl(diceBearBaseUrl).build();
    }
}
