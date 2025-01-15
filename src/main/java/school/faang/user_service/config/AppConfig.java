package school.faang.user_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AppConfig {
    @Value("${app.config.min_skill_offers:3}")
    private int minSkillOffers;

    @Value("${app.config.skill_acquire_rating:2}")
    private int skillAcquireRating;

    @Value("${app.config.subscription_follow_rating:1}")
    private int followRating;
}
