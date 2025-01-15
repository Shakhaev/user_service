package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.utility.validator.impl.premium.PremiumRequestValidator;

@RequiredArgsConstructor
@RequestMapping("api/v1/premium")
@RestController
public class PremiumController {
    private final PremiumRequestValidator validator;
    private final UserContext userContext;
    private final PremiumService premiumService;
    private final PremiumMapper premiumMapper;

    @PostMapping("/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public PremiumDto buyPremium(@RequestParam int days) {
        validator.validate(days);
        PremiumPeriod period = PremiumPeriod.fromDays(days);
        Long userId = userContext.getUserId();

        Premium premium = premiumService.buyPremium(userId, period);
        return premiumMapper.toDto(premium);
    }
}
