package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.constant.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    @Override
    public Object buyPremium(PremiumPeriod premiumPeriod) {
        return null;
    }
}
