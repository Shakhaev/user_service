package school.faang.user_service.service.promotion;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.mapper.promotion.PromotionPlanMapper;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionPlanServiceImpl implements PromotionPlanService {
    private final PromotionPlanRepository repository;
    private final PromotionPlanMapper mapper;

    @Cacheable(value = "promotionPlans")
    @Override
    public List<PromotionPlanDto> getPromotionPlans() {
        log.info("Get promotion plans");
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Cacheable(value = "promotionPlansByName", key = "#name")
    @Override
    public PromotionPlanDto getPromotionPlanByName(String name) {
        log.info("Get promotion plan by name: {}", name);
        PromotionPlan promotionPlan = repository.findPromotionPlanByName(name).orElseThrow(() ->
                new EntityNotFoundException(String.format("Promotion plan with name = %s not found", name)));
        return mapper.toDto(promotionPlan);
    }

    @Cacheable(value = "promotionPlansByPrice", key = "#price")
    @Override
    public PromotionPlanDto getPromotionPlanByPrice(long price) {
        log.info("Get promotion plan by price: {}", price);
        PromotionPlan promotionPlan = repository.findPromotionPlanByPrice(price).orElseThrow(() ->
                new EntityNotFoundException(String.format("Promotion plan with price = %d not found", price)));
        return mapper.toDto(promotionPlan);
    }
}
