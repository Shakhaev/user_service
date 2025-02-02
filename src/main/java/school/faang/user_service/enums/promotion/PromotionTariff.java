package school.faang.user_service.enums.promotion;

import lombok.Getter;

@Getter
public enum PromotionTariff {
    BASIC("BASIC"),
    PREMIUM("PREMIUM");

    private final String value;

    PromotionTariff(String value) {
        this.value = value;
    }
}
