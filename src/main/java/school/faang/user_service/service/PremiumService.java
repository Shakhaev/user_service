package school.faang.user_service.service;

import school.faang.user_service.constant.PremiumPeriod;
import school.faang.user_service.dto.PremiumDto;

public interface PremiumService {
    PremiumDto buyPremium(Long userId, PremiumPeriod premiumPeriod);
}
