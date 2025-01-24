package school.faang.user_service.service.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final WebClient webClient;

    public <T> Mono<T> get(String url, Class<T> responseType, Object... uriVariables) {
        return webClient.get()
                .uri(url, uriVariables)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, ex -> Mono.empty());
    }
}
