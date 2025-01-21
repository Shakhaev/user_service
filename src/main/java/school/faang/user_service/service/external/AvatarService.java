package school.faang.user_service.service.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private static final String DICEBEAR_URL = "https://api.dicebear.com/9.x/adventurer/svg";
    private final Random random = new Random();
    private final ExternalApiService externalApiService;

    public Mono<String> getRandomAvatar() {
        int seed = random.nextInt();
        return externalApiService.get(DICEBEAR_URL, String.class, seed);
    }
}
