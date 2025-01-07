package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventDto create(EventDto event) {

        List<Skill> UserSkills = checkGetUserSkills(event);

        Event newEventCandidate = eventMapper.toEntityEvent(event);

        newEventCandidate.setRelatedSkills(UserSkills);

        newEventCandidate.setOwner(userRepository.findById(event.getOwnerId()).get());

        Event newSaveEvent = eventRepository.save(newEventCandidate);

        return eventMapper.toDto(newSaveEvent);

    }

    private List<Skill> checkGetUserSkills(EventDto event) throws DataValidationException {
        Map<User,List<Skill>> userSkills = new HashMap<>();
        Long ownerId = event.getOwnerId();

        User skillOwner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataValidationException("User not found"));


        if (!skillOwner.getSkills().stream()
                .map(Skill::getId)
                .anyMatch(id -> id.equals(skillOwner.getId()))) {
            throw new DataValidationException("User has no skills!");
        }

        return skillOwner.getSkills();
    }

    public Event updateEvent(EventDto event) {
        return eventMapper.toEntityEvent(event);
    }


}
