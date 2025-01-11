package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserService;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final UserService userService;

    public void validateEventCreatorSkills(EventCreateDto eventCreateDto) {
        UserDto owner = userService.findUserById(eventCreateDto.getOwnerId());
        var ownerSkills = new HashSet<>(owner.getSkillsIds());
        var necessarySkills = eventCreateDto.getRelatedSkillsIds();
        if (!ownerSkills.containsAll(necessarySkills)) {
            throw new DataValidationException("Owner should have all related skills");
        }
    }

    public void validateEventInfo(Event event) {
        var title = event.getTitle();
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Event should contain title" + title);
        } else if (event.getStartDate() == null) {
            throw new DataValidationException("Event should contain start date" + title);
        }
    }
}
