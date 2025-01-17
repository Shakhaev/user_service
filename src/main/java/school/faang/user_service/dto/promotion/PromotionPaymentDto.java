package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;
import school.faang.user_service.enums.promotion.PromotionPaymentType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionPaymentDto {

    private UUID id;

    private long userId;

    private BigDecimal amount;

    private PromotionPaymentStatus status;

    private PromotionPaymentType paymentType;
}