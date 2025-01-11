package school.faang.user_service.exception.promotion;

import school.faang.user_service.exception.global.ApiException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PromotionNotFoundException extends ApiException {
    private static final String MESSAGE = "Promotion tariff of %s views not found, please select between: %s";

    public PromotionNotFoundException(Integer numberOfViews, List<Integer> viewsOption) {
        super(MESSAGE, NOT_FOUND, numberOfViews, viewsOption);
    }
}
