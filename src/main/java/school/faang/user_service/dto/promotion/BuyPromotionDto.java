package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.promotion.PromotionPlan;

@Data
public class  BuyPromotionDto {
    @NotBlank
    private String paymentMethod;
    @Positive
    private long userId;
    @NotNull
    private PromotionPlan plan;
}
