package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class EventValidator {

    public void validate(EventDto event) {
        if (event.title() == null || event.title().isEmpty()) {
            throw new DataValidationException("Event title must not be null or empty.");
        }
        if (event.startDate() == null) {
            throw new DataValidationException("Event start date must not be null.");
        }
        if (event.ownerId() == null) {
            throw new DataValidationException("Event must have an owner.");
        }
    }
}
