package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.service.promotion.PromotionPlanService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${promotions.domain.path}/promotion-plans")
public class PromotionPlanController {
    private final PromotionPlanService service;

    @GetMapping
    public List<PromotionPlanDto> getPromotionPlans() {
        return service.getPromotionPlans();
    }

    @GetMapping("/by-name/{name}")
    public PromotionPlanDto getPromotionPlanByName(@PathVariable("name") String name) {
        return service.getPromotionPlanByName(name);
    }

    @GetMapping("/by-price/{price}")
    public PromotionPlanDto getPromotionPlanByPrice(@PathVariable("price") long price) {
        return service.getPromotionPlanByPrice(price);
    }
}
