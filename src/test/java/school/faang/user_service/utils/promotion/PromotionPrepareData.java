package school.faang.user_service.utils.promotion;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionPayment;
import school.faang.user_service.entity.promotion.PromotionPlan;

import java.util.UUID;

@UtilityClass
public class PromotionPrepareData {
    private static final UUID RANDOM_UUID = UUID.randomUUID();
    private static final String PLAN_NAME = "BASIC";

    public static PromotionPayment getPromotionPayment() {
        return PromotionPayment.builder()
                .id(RANDOM_UUID)
                .build();
    }

    public static PromotionPaymentDto getPromotionPaymentDto() {
        return PromotionPaymentDto.builder()
                .id(RANDOM_UUID)
                .build();
    }

    public static PromotionPlanDto getPromotionPlanDto() {
        return PromotionPlanDto.builder()
                .name(PLAN_NAME)
                .build();
    }

    public static PromotionPlan getPromotionPlan() {
        return PromotionPlan.builder()
                .name(PLAN_NAME)
                .build();
    }

    public static PromotionRequestDto getPromotionRequestDto() {
        return PromotionRequestDto.builder()
                .userId(1L)
                .build();
    }

    public static PromotionResponseDto getPromotionResponseDto() {
        return PromotionResponseDto.builder()
                .userId(1L)
                .paymentId(RANDOM_UUID)
                .build();
    }

    public static Promotion getPromotion() {
        return Promotion.builder()
                .userId(1L)
                .promotionPayment(PromotionPayment.builder()
                        .id(RANDOM_UUID)
                        .build())
                .build();
    }
}
