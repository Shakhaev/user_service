package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.redis.events.PremiumBoughtEvent;
import school.faang.user_service.config.redis.events.publisher.PremiumBoughtEventPublisher;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {

    private final PremiumRepository premiumRepository;
    private final PremiumBoughtEventPublisher eventPublisher;

    @Async("removeExpiredPremiumAccess")
    public void removeExpiredPremium(List<Premium> batch) {
        premiumRepository.deleteAll(batch);
    }

    public void buyPremium(Long userId, double amount, int duration) {
        log.info("Processing premium purchase for user {}: amount {}, duration {}", userId, amount, duration);
        eventPublisher.publish(userId, amount, duration);
    }
}
