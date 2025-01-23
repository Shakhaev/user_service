package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.promotion.PromotionPlan;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  BuyPromotionDto {
    @NotBlank
    private String paymentMethod;
    @Positive
    private long userId;
    @NotNull
    private PromotionPlan plan;
}
