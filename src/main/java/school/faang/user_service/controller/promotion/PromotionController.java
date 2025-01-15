package school.faang.user_service.controller.promotion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.service.promotion.PromotionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("${promotions.domain.path}/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping
    public PromotionDto createPromotion(@RequestBody @Valid PromotionDto promotionDto) {
        return promotionService.createPromotion(promotionDto);
    }
}
