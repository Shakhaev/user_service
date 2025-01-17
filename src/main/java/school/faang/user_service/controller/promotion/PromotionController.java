package school.faang.user_service.controller.promotion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.service.promotion.PromotionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${promotions.domain.path}/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping("/by-user/{id}")
    public List<PromotionDto> getPromotionsByUser(@PathVariable("id") long userId) {
        return promotionService.getPromotionsByUser(userId);
    }

    @PostMapping
    public PromotionDto createPromotion(@RequestBody @Valid PromotionDto promotionDto) {
        return promotionService.createPromotion(promotionDto);
    }
}
