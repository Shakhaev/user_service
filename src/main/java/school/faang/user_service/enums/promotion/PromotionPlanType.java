package school.faang.user_service.enums.promotion;

import lombok.Getter;

@Getter
public enum PromotionPlanType {
    BASIC("BASIC"),
    PREMIUM("PREMIUM");

    private final String value;

    PromotionPlanType(String value) {
        this.value = value;
    }
}
