package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    @Autowired
    public EventParticipationService (EventParticipationRepository eventParticipationRepository){
        this.eventParticipationRepository = eventParticipationRepository;
    }

    public void registerParticipant(long eventId, long userId) throws Exception{
        try {
            if(eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                    .noneMatch(user -> user.getId() == userId)){
                eventParticipationRepository.register(eventId, userId);
            }
        }catch (Exception e){
            throw new Exception(e);
        }
    }

}
