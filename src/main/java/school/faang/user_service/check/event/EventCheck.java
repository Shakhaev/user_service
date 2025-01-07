package school.faang.user_service.check.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventCheck {
    private final UserService userService;

    public void eventCheck(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isEmpty()) {
            throw new DataValidationException("Event title не может быть пустым");
        } else if (event.getTitle().length() > 64) {
            throw new DataValidationException("Длина Event title не может быть больше 64");
        }
        if (event.getDescription() == null || event.getDescription().isEmpty()) {
            throw new DataValidationException("Event description не может быть пустым");
        } else if (event.getDescription().length() > 4096) {
            throw new DataValidationException("Длина Event description не может быть больше 4096");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("StartDate не может быть пустым");
        }
        if (event.getEndDate() == null) {
            throw new DataValidationException("EndDate не может быть пустым");
        }
        if (event.getLocation() == null || event.getLocation().isEmpty()) {
            throw new DataValidationException("Event Location не может быть пустым");
        } else if (event.getLocation().length() > 128) {
            throw new DataValidationException("Длина Event Location не может быть больше 128");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("OwnerId не может быть пустым");
        }
        if (event.getEventType() == null) {
            throw new DataValidationException("EventType не может быть пустым");
        }
        if (event.getEventStatus() == null) {
            throw new DataValidationException("EventStatus не может быть пустым");
        }
    }

    public boolean userHasSkills(Long ownerId, List<Long> relatedSkillIds) {
        User user = userService.getUserById(ownerId);
        Set<Long> userSkillIdList = user.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
        return userSkillIdList.containsAll(relatedSkillIds);
    }
}
