package school.faang.user_service.controller;

import jakarta.persistence.criteria.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.payment.OrderDto;
import school.faang.user_service.dto.promotion.BuyPromotionDto;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.service.PromotionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    public OrderDto buyPromotion(@RequestBody BuyPromotionDto buyPromotionDto) {
        return promotionService.buyPromotion(buyPromotionDto);
    }

    public PromotionDto activatePromotion(@PathVariable Long promotionId, @RequestBody BuyPromotionDto buyPromotionDto) {
        return promotionService.activatePromotion(promotionId, buyPromotionDto);
    }

    public List<PromotionDto> getAllPromotionsForUser(@PathVariable Long userId) {
        return promotionService.getAllPromotionsForUser(userId);
    }

    public PromotionDto updatePromotion(@RequestBody BuyPromotionDto buyPromotionDto, @PathVariable Long promotionId) {
        return promotionService.updatePromotion(buyPromotionDto, promotionId);
    }

    public void deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
    }
}
