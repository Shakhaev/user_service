package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final SkillService skillService;

    public void validateCreatorSkills(EventCreateDto eventCreateDto) {
        var ownerSkills = skillService.findSkillsByUserId(eventCreateDto.getOwnerId())
                .stream()
                .map(SkillDto::getId)
                .collect(Collectors.toCollection(HashSet::new));
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
