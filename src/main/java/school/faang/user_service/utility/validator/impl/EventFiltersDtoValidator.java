package school.faang.user_service.utility.validator.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.utility.validator.AbstractDataValidator;

@Component
public class EventFiltersDtoValidator extends AbstractDataValidator<EventFiltersDto> {
    @Override
    public void validate(EventFiltersDto filters) {
        checkNotNull(filters, "Event filter cannot be null");
    }
}
