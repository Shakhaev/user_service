package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.Currency;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;

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

    private Currency currency;

    private PromotionPaymentStatus status;

}