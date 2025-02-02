package school.faang.user_service.enums.promotion;

import lombok.Getter;

@Getter
public enum PromotionPlanType {
    USER("USER"),
    EVENT("EVENT");

    private final String value;

    PromotionPlanType(String value) {
        this.value = value;
    }
}