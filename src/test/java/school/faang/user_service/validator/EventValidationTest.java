package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidationTest {

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventValidation eventValidation;

    @Test
    void testValidateEvent() {
        EventDto eventDto = null;

        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    void testValidateEventWithNullTitle() {
        EventDto eventDto = prepareData();
        eventDto.setTitle(null);

        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    void testValidateEventWithBlankTitle() {
        EventDto eventDto = prepareData();
        eventDto.setTitle("  ");

        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    void testValidateEventWithNullStartDate() {
        EventDto eventDto = prepareData();
        eventDto.setStartDate(null);

        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    void testValidateEventWithPastStartDate() {
        EventDto eventDto = prepareData();
        eventDto.setStartDate(LocalDateTime.now().minusMinutes(10));

        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    void testValidateEventWhereEndDateBeforeStartDate() {
        EventDto eventDto = prepareData();
        eventDto.setStartDate(LocalDateTime.now().plusDays(1));
        eventDto.setEndDate(LocalDateTime.now().minusDays(10));

        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    void testValidateEventWithNullOwnerId() {
        EventDto eventDto = prepareData();
        eventDto.setOwnerId(null);

        assertThrows(DataValidationException.class, () -> eventValidation.validateEvent(eventDto));
    }

    @Test
    void testValidateUserWithAllSkills() {
        EventDto eventDto = new EventDto();
        Skill requiredSkill = Skill.builder()
                .id(1L)
                .title("Required skill")
                .build();
        Skill userSkill = Skill.builder()
                .id(1L)
                .title("Required skill")
                .build();

        when(eventMapper.toEntity(eventDto)).thenReturn(Event.builder()
                .owner(User.builder()
                        .skills(List.of(userSkill))
                        .build())
                .relatedSkills(List.of(requiredSkill))
                .build());

        assertDoesNotThrow(() -> eventValidation.validateUserSkills(eventDto));
    }

    @Test
    void testValidateUserWithLackSkills() {
        EventDto eventDto = prepareData();
        Skill requiredSkill = Skill.builder()
                .id(1L)
                .title("Required skill")
                .build();
        Skill userSkill = Skill.builder()
                .id(2L)
                .title("User's skill")
                .build();

        when(eventMapper.toEntity(eventDto)).thenReturn(Event.builder()
                .owner(User.builder()
                        .skills(List.of(userSkill))
                        .build())
                .relatedSkills(List.of(requiredSkill))
                .build());

        assertThrows(DataValidationException.class, () -> eventValidation.validateUserSkills(eventDto));
    }

    @Test
    void testValidateEventIdWithNull() {
        Long id = null;

        assertThrows(DataValidationException.class, () -> eventValidation.validateEventId(id));
    }

    @Test
    void testValidateEventWithNonExistingId() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        when(eventRepository.existsById(eventDto.getId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> eventValidation.validateEventId(eventDto.getId()));
    }

    @Test
    void testValidateEventOwnerIdWithNull() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(null);

        assertThrows(DataValidationException.class, () -> eventValidation.validateEventOwner(eventDto));
    }

    @Test
    void testValidateEventOwnerWithWrongOwner() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setOwnerId(2L);

        Event existingEvent = new Event();
        existingEvent.setId(1L);
        User owner = new User();
        owner.setId(1L);
        existingEvent.setOwner(owner);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        assertThrows(DataValidationException.class, () -> eventValidation.validateEventOwner(eventDto));
    }

    private EventDto prepareData() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("title");
        eventDto.setStartDate(LocalDateTime.now().plusDays(1));
        eventDto.setEndDate(LocalDateTime.now().plusDays(10));
        eventDto.setOwnerId(1L);
        return eventDto;
    }
}