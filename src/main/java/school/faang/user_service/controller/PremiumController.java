package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.BuyPremiumDto;
import school.faang.user_service.dto.premium.PremiumPlan;
import school.faang.user_service.service.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/premium")
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping
    private String buyPremium(@RequestBody @Valid BuyPremiumDto dto) {
        return premiumService.buyPremium(dto.userId(), PremiumPlan.fromDays(dto.days()), dto.paymentMethod());
    }

    @PostMapping("activate")
    private void activatePremiumForUser(@RequestParam Long orderId) {
        premiumService.activatePremiumForUser(orderId);
    }
}
