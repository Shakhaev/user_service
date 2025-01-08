package school.faang.user_service.utility.validator.event.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.utility.validator.DataValidator;

@Component
@RequiredArgsConstructor
public class EventFilterValidator implements DataValidator<EventFilters> {
    private final EventFilterValidatorUtils filterValidatorUtils;

    @Override
    public void validate(EventFilters filters) {
        filterValidatorUtils.checkNotNull(filters, "Event filter cannot be null");
    }
}
