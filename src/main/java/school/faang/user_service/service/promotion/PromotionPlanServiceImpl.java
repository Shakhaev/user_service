package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.mapper.promotion.PromotionPlanMapper;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionPlanServiceImpl implements PromotionPlanService {
    private final PromotionPlanRepository repository;
    private final PromotionPlanMapper mapper;
    private final CacheManager cacheManager;

    @Cacheable(value = "promotionPlans")
    @Override
    public List<PromotionPlanDto> getPromotionPlans() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Cacheable(value = "promotionPlansByName", key = "#name")
    @Override
    public PromotionPlanDto getPromotionPlanByName(String name) {
        return mapper.toDto(repository.findPromotionPlanByName(name));
    }

    @Cacheable(value = "promotionPlansByPrice", key = "#price")
    @Override
    public PromotionPlanDto getPromotionPlanByPrice(long price) {
        return mapper.toDto(repository.findPromotionPlanByPrice(price));
    }
}
