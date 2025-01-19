package school.faang.user_service.dto.premium.message.builder;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.message.PremiumBoughtMessage;

import java.time.LocalDateTime;

@Component
public class PremiumBoughtMessageBuilder {
    public PremiumBoughtMessage build(Long userId, Double cost, Integer days) {
        return PremiumBoughtMessage.builder()
                .userId(userId)
                .cost(cost)
                .days(days)
                .purchaseDate(LocalDateTime.now())
                .build();
    }
}
