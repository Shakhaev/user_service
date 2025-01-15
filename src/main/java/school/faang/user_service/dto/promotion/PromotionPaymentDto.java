package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;
import school.faang.user_service.enums.promotion.PromotionPaymentType;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionPaymentDto {

    private long id;

    private long userId;

    private long promotionId;

    private BigDecimal amount;

    private PromotionPaymentStatus status;

    private PromotionPaymentType paymentType;
}