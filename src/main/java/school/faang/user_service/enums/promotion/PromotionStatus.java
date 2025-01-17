package school.faang.user_service.enums.promotion;

import lombok.Getter;

@Getter
public enum PromotionStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String value;

    PromotionStatus(String value) {
        this.value = value;
    }

}
