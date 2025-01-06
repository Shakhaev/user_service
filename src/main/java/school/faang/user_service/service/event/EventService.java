package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
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

        checkUserSkills(event);

        Event newEventCandidate = eventMapper.toEntityEvent(event);

        newEventCandidate.setRelatedSkills(checkUserSkills(event).get(userRepository.findById(event.getOwnerId())));

        newEventCandidate.setOwner(userRepository.findById(event.getOwnerId()).get());

        Event newSaveEvent = eventRepository.save(newEventCandidate);

        return eventMapper.toDto(newSaveEvent);

    }

    private Map<User,List<Skill>> checkUserSkills(EventDto event) throws DataIntegrityViolationException {
        Map<User,List<Skill>> userSkills = new HashMap<>();
        Long ownerId = event.getOwnerId();
        User skillOwner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataIntegrityViolationException("User not found"));


        if (!skillOwner.getSkills().stream()
                .map(Skill::getId)
                .anyMatch(id -> id.equals(skillOwner.getId()))) {
            throw new DataIntegrityViolationException("User has no skills!");
        }

        List<Skill> skills = skillOwner.getSkills();

        return (Map<User, List<Skill>>) userSkills.put(skillOwner,skills);
    }

    private Map.Entry getUserSkills(EventDto event) {
        Map<User, List<Skill>> userSkills = new HashMap<>();
        userSkills.put(
                userRepository.findById(event.getOwnerId()).get(),
                userRepository.findById(event.getOwnerId()).get().getSkills());
        return userSkills.entrySet().stream().findFirst().orElse(null);
    }

    public Event updateEvent(EventDto event) {
        return eventMapper.toEntityEvent(event);
    }


}
