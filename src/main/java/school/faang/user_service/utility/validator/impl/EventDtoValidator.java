package school.faang.user_service.utility.validator.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.utility.validator.AbstractDataValidator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Component
public class EventDtoValidator extends AbstractDataValidator<EventDto> {

    @Override
    public void validate(EventDto eventDto) {
        checkNotNull(eventDto, "Event cannot be null");
        checkStringNotNullOrEmpty(eventDto.title(), "Event name cannot be null or empty");
        checkStringNotNullOrEmpty(eventDto.description(), "Description cannot be null or empty");
        checkNotNull(eventDto.startDate(), "Event start date cannot be null");
        checkFutureDate(eventDto.startDate(), "Event start date cannot be in the past");
        checkNotNull(eventDto.endDate(), "Event end date cannot be null");
        checkFutureDate(eventDto.endDate(), "Event end date cannot be in the past");
        checkChronology(eventDto.startDate(), eventDto.endDate(),
                "Event end date cannot be before the start date");
        checkStringNotNullOrEmpty(eventDto.location(), "Location cannot be null or empty");
        checkNotNull(eventDto.ownerId(), "Event must have an owner");
        checkCollectionNotNullOrEmpty(eventDto.relatedSkillIds(),
                "Related skills cannot be null or empty");
        checkNotNull(eventDto.type(), "Event type cannot be null");
        checkNotNull(eventDto.status(), "Event status cannot be null");
        checkEnumValue(eventDto.type(), EventType.values(), "Invalid event type: " + eventDto.type());
        checkEnumValue(eventDto.status(), EventStatus.values(),
                "Invalid event status: " + eventDto.status());
    }

    public void checkFutureDate(LocalDateTime date, String errorMessage) {
        if (date.isBefore(LocalDateTime.now())) {
            throw new DataValidationException(errorMessage);
        }
    }

    public void checkChronology(LocalDateTime startDate, LocalDateTime endDate, String errorMessage) {
        if (endDate.isBefore(startDate)) {
            throw new DataValidationException(errorMessage);
        }
    }

    public <T extends Enum<T>> void checkEnumValue(Enum<?> value, Enum<?>[] validValues, String errorMessage) {
        if (Arrays.stream(validValues).noneMatch(validValue -> Objects.equals(validValue, value))) {
            throw new DataValidationException(errorMessage);
        }
    }
}
