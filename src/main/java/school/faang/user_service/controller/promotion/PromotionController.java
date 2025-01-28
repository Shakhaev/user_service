package school.faang.user_service.controller.promotion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.service.promotion.PromotionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping("/by-user/{id}")
    public List<PromotionResponseDto> getPromotionsByUser(@PathVariable("id") long userId) {
        return promotionService.getPromotionsByUser(userId);
    }

    @GetMapping("/by-event/{id}")
    public List<PromotionResponseDto> getPromotionsByEvent(@PathVariable("id") long eventId) {
        return promotionService.getPromotionsByEvent(eventId);
    }

    @PostMapping
    public PromotionResponseDto createPromotion(@RequestBody @Valid PromotionRequestDto promotionDto) {
        return promotionService.createPromotion(promotionDto);
    }
}
