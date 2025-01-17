package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.mapper.promotion.PromotionPlanMapper;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionPlanServiceImpl implements PromotionPlanService {
    private final PromotionPlanRepository repository;
    private final PromotionPlanMapper mapper;

    @Override
    public List<PromotionPlanDto> getPromotionPlans() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public PromotionPlan getPromotionPlanByName(String promotionPlanType) {
        return repository.findPromotionPlanByName(promotionPlanType);
    }
}
