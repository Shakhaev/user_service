package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.service.PromotionService;

import java.util.List;

@EnableFeignClients
@RestController
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionDto createPromotion(@RequestBody PromotionRequestDto requestDTO) {
        return promotionService.createPromotion(requestDTO);
    }

    public List<PromotionDto> getAllPromotionsForUser(@PathVariable Long userId) {
        return promotionService.getAllPromotionsForUser(userId);
    }

    public PromotionDto updatePromotion(@PathVariable Long promotionId,
                                        @RequestBody PromotionRequestDto requestDTO) {
        return promotionService.updatePromotion(requestDTO, promotionId);
    }

    public void deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
    }
}
