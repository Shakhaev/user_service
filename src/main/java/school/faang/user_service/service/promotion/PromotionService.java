package school.faang.user_service.service.promotion;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionDto;

import java.util.List;

@Service
public interface PromotionService {
    PromotionDto createPromotion(PromotionDto promotionDto);

    List<PromotionDto> getPromotionsByUser(long userId);
}
