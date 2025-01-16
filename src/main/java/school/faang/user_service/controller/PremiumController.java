package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.constant.PremiumPeriod;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.service.PremiumService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/premium")
public class PremiumController {
    private final PremiumService premiumService;
    private final UserContext userContext;

    @PutMapping(value = "/buy/{days}")
    public PremiumDto buyPremium(@PathVariable @Positive(message = "Days cannot be negative") Integer days) {
        Long userId = userContext.getUserId();
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);

        return premiumService.buyPremium(userId, premiumPeriod);
    }
}
