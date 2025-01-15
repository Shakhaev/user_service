package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.promotion.PromotionRepository;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    public PromotionDto createPromotion(PromotionDto promotionDto) {
        Promotion entity = promotionMapper.toEntity(promotionDto);
        return promotionMapper.toDto(promotionRepository.save(entity));
    }
}
