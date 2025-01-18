package school.faang.user_service.service.promotion;

import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.dto.promotion.PromotionRequestDto;

import java.util.UUID;

public interface PromotionPaymentService {

    PromotionPaymentDto sendAndCreate(PromotionRequestDto dto);

    PromotionPaymentDto getPromotionPaymentById(UUID id);
}
