package school.faang.user_service.utility.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.utility.validator.DataValidator;

@Component
@RequiredArgsConstructor
public class EventDtoValidator implements DataValidator<EventDto> {
    private final EventValidatorUtils eventValidatorUtils;

    @Override
    public void validate(EventDto event) {
        eventValidatorUtils.checkNotNull(event, "Event cannot be null");
        eventValidatorUtils.checkStringNotNullOrEmpty(event.title(), "Event name cannot be null or empty");
        eventValidatorUtils.checkStringNotNullOrEmpty(event.description(), "Description cannot be null or empty");
        eventValidatorUtils.checkNotNull(event.startDate(), "Event start date cannot be null");
        eventValidatorUtils.checkFutureDate(event.startDate(), "Event start date cannot be in the past");
        eventValidatorUtils.checkNotNull(event.endDate(), "Event end date cannot be null");
        eventValidatorUtils.checkFutureDate(event.endDate(), "Event end date cannot be in the past");
        eventValidatorUtils.checkChronology(event.startDate(), event.endDate(),
                "Event end date cannot be before the start date");
        eventValidatorUtils.checkStringNotNullOrEmpty(event.location(), "Location cannot be null or empty");
        eventValidatorUtils.checkNotNull(event.ownerId(), "Event must have an owner");
        eventValidatorUtils.checkCollectionNotNullOrEmpty(event.relatedSkillIds(),
                "Related skills cannot be null or empty");
        eventValidatorUtils.checkNotNull(event.type(), "Event type cannot be null");
        eventValidatorUtils.checkNotNull(event.status(), "Event status cannot be null");
        eventValidatorUtils.checkEnumValue(event.type(), EventType.values(), "Invalid event type: " + event.type());
        eventValidatorUtils.checkEnumValue(event.status(), EventStatus.values(),
                "Invalid event status: " + event.status());
    }
}
