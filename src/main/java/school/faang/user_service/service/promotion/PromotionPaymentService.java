package school.faang.user_service.service.promotion;

import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.entity.promotion.PromotionPayment;

public interface PromotionPaymentService {

    PromotionPayment create(PromotionPayment promotionPayment);

    PromotionPaymentDto getPromotionPaymentById(String id);
}
