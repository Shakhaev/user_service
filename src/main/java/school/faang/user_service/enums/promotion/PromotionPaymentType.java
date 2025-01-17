package school.faang.user_service.enums.promotion;

import lombok.Getter;

@Getter
public enum PromotionPaymentType {
    USER("USER"),
    EVENT("EVENT");

    private final String value;

    PromotionPaymentType(String value) {
        this.value = value;
    }
}