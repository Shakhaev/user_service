package school.faang.user_service.service.promotion;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;

import java.util.List;

@Service
public interface PromotionService {
    PromotionResponseDto createPromotion(PromotionRequestDto promotionDto);

    List<PromotionResponseDto> getPromotionsByUser(long userId);
}
