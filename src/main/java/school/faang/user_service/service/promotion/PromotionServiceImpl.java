package school.faang.user_service.service.promotion;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionPayment;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;
import school.faang.user_service.enums.promotion.PromotionPaymentType;
import school.faang.user_service.enums.promotion.PromotionStatus;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionPlanService promotionPlanService;
    private final PromotionPaymentService promotionPaymentService;
    private final PromotionMapper promotionMapper;

    @Override
    public List<PromotionDto> getPromotionsByUser(long userId) {
        return promotionRepository.getPromotionByUserId(userId).stream()
                .map(promotionMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PromotionDto createPromotion(PromotionDto dto) {
        var promotionPlanType = dto.getPromotionPlanType();
        var promotionPlan = promotionPlanService.getPromotionPlanByName(promotionPlanType.getValue());

        PromotionPayment promotionPayment = new PromotionPayment();
        promotionPayment.setId(UUID.randomUUID().toString());
        promotionPayment.setUserId(dto.getUserId());
        promotionPayment.setPaymentType(PromotionPaymentType.USER);
        promotionPayment.setStatus(PromotionPaymentStatus.PENDING);
        promotionPayment.setAmount(dto.getMoney());
        promotionPaymentService.create(promotionPayment);
        //todo: promotion payment
        var promotion = new Promotion();
        promotion.setUserId(dto.getUserId());
        promotion.setEventId(dto.getEventId());
        promotion.setPromotionPlanType(promotionPlanType);
        promotion.setRemainingViews(promotionPlan.getViewsCount());
        promotion.setStatus(PromotionStatus.ACTIVE.getValue());
        promotion.setPromotionPayment(promotionPayment);
        return promotionMapper.toDto(promotionRepository.save(promotion));

    }
}
