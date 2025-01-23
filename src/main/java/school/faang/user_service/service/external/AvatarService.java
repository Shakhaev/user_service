package school.faang.user_service.service.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import school.faang.user_service.config.AppConfig;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final Random random = new Random();
    private final ExternalApiService externalApiService;
    private final AppConfig appConfig;

    public Mono<String> getRandomAvatar() {
        int seed = Math.abs(random.nextInt());
        return externalApiService.get(appConfig.getDICEBEAR_URL(), String.class, seed);
    }
}
