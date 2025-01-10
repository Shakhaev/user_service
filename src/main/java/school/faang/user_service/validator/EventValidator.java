package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserService;

import java.util.HashSet;

@RequiredArgsConstructor
public class EventValidator {
    private final UserService userService;

    public void validateEventCreatorSkills(EventDto eventDto) {
        UserDto owner = userService.findUserById(eventDto.getOwnerId());
        var ownerSkills = new HashSet<>(owner.getSkillsIds());
        var necessarySkills = eventDto.getRelatedSkillsIds();
        if (!ownerSkills.containsAll(necessarySkills)) {
            throw new DataValidationException("Owner should have all related skills");
        }
    }

    public void validateEventInfo(EventDto eventDto) {
        var title = eventDto.getTitle();
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Event should contain title");
        } else if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Event should contain start date");
        } else if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("Event should contain owner (id)");
        }
    }
}
