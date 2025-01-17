package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.Currency;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionTariff;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRequestDto {

    @NotNull
    private Long userId;

    private Long eventId;

    @NotNull
    private PromotionTariff tariff;

    @NotNull
    private PromotionPlanType planType;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;

}
