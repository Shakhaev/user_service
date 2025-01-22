package school.faang.user_service.validation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class EventValidation {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    public void validateEvent(EventDto eventDto) {
        if (eventDto == null) {
            throw new DataValidationException("Событие не найдено.");
        }
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Название события обязательно и не может быть пустым.");
        }
        if (eventDto.getStartDate() == null || eventDto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new DataValidationException("Укажите дату начала события, она должна быть в будущем.");
        }
        if (eventDto.getEndDate() != null && eventDto.getEndDate().isBefore(eventDto.getStartDate())) {
            throw new DataValidationException("Дата начала события должна быть ранее даты окончания события.");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("Требуется ID владельца события.");
        }
    }

    public void validateUserSkills(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        User user = event.getOwner();
        if (!new HashSet<>(user.getSkills()).containsAll(event.getRelatedSkills())) {
            throw new DataValidationException("Пользователь не имеет необходимых навыков для этого события.");
        }
    }

    public void validateEventId(Long id) {
        if (id == null) {
            throw new DataValidationException("ID не может быть null.");
        }
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Не существует события с ID: " + id);
        }
    }

    public void validateEventOwner(EventDto eventDto) {
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("ID владельца события не может быть null.");
        }
        Event existingEvent = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new DataValidationException("Событие не найдено."));
        if (!existingEvent.getOwner().getId().equals(eventDto.getOwnerId())) {
            throw new DataValidationException("Пользователь не является владельцем события.");
        }
    }
}
