package school.faang.user_service.exception.premium;

import school.faang.user_service.exception.global.ApiException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PremiumNotFoundException extends ApiException {
    private static final String MESSAGE = "Premium period in %s days not found, please select among: %s";

    public PremiumNotFoundException(Integer days, List<Integer> daysOptions) {
        super(MESSAGE, NOT_FOUND, days, daysOptions);
    }
}
