package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.constant.PremiumPeriod;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.service.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premium")
public class PremiumController {
    private final PremiumService premiumService;
    private final UserContext userContext;

    @PutMapping(value = "/buy/{days}")
    public PremiumDto buyPremium(@PathVariable Integer days) {
        Long userId = userContext.getUserId();
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);

        return premiumService.buyPremium(userId, premiumPeriod);
    }
}
