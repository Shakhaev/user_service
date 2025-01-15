package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.enums.promotion.PromotionStatus;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.promotion.PromotionRepository;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionPlanService promotionPlanService;
    private final PromotionMapper promotionMapper;

    @Override
    public PromotionDto createPromotion(PromotionDto dto) {
        var promotionPlanType = dto.getPromotionPlanType();
        var promotionPlan = promotionPlanService.getPromotionPlanByName(promotionPlanType);

        //todo: promotion payment
        var promotion = new Promotion();
        promotion.setUserId(dto.getUserId());
        promotion.setEventId(dto.getEventId());
        promotion.setPromotionPlanType(promotionPlanType);
        promotion.setRemainingViews(promotionPlan.getViewsCount());
        promotion.setStatus(PromotionStatus.ACTIVE);
        promotion.setPaymentId(1L);
        return promotionMapper.toDto(promotionRepository.save(promotion));

    }
}
