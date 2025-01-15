package school.faang.user_service.utility.validator.impl.premium;

import org.springframework.stereotype.Component;
import school.faang.user_service.utility.validator.AbstractDataValidator;

@Component
public class PremiumRequestValidator extends AbstractDataValidator<Integer> {
    @Override
    public void validate(Integer data) {
        checkNumberIsPositive(data, "Days amount must be greater than 0");
    }
}
