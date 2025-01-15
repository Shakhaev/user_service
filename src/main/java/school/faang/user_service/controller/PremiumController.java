package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.BuyPremiumDto;
import school.faang.user_service.dto.premium.Plan;
import school.faang.user_service.service.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/premium")
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping
    private String buyPremium(@RequestBody @Valid BuyPremiumDto dto) {
        return premiumService.buyPremium(dto.userId(), Plan.fromDays(dto.days()), dto.paymentMethod());
    }
}
