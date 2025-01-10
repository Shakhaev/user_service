package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @InjectMocks
    private EventValidator eventValidator;

    @Mock
    private UserService userService;

    @Test
    void shouldThrowOnCreatorWithoutNecessarySkills() {
        UserDto userDto = new UserDto();
        userDto.setSkillsIds(List.of(1L, 2L));
        Mockito.when(userService.findUserById(Mockito.anyLong())).thenReturn(userDto);

        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkillsIds(List.of(2L, 3L));

        Assertions.assertThrows(DataValidationException.class, () -> eventValidator.validateEventCreatorSkills(eventDto));
    }

    @Test
    void shouldNotThrowWithCorrectCreatorSkills() {
        UserDto userDto = new UserDto();
        userDto.setSkillsIds(List.of(1L, 2L, 3L));
        Mockito.when(userService.findUserById(Mockito.anyLong())).thenReturn(userDto);

        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkillsIds(List.of(1L, 2L));

        Assertions.assertDoesNotThrow(() -> eventValidator.validateEventCreatorSkills(eventDto));
    }

    @Test
    void shouldThrowWithBlankTitle() {
        EventDto event = new EventDto();
        event.setTitle(" \n\t ");
        Assertions.assertThrows(DataValidationException.class, () -> eventValidator.validateEventInfo(event));
    }

    @Test
    void shouldThrowWithNullTitle() {
        EventDto event = new EventDto();
        Assertions.assertThrows(DataValidationException.class, () -> eventValidator.validateEventInfo(event));
    }

    @Test
    void shouldThrowWithNullDate() {
        EventDto event = new EventDto();
        event.setTitle("title");
        Assertions.assertThrows(DataValidationException.class, () -> eventValidator.validateEventInfo(event));
    }

    @Test
    void shouldThrowWithoutOwner() {
        EventDto event = new EventDto();
        event.setTitle("title");
        event.setStartDate(LocalDateTime.now());
        Assertions.assertThrows(DataValidationException.class, () -> eventValidator.validateEventInfo(event));
    }

    @Test
    void shouldNotThrowWithCorrectEvent() {
        EventDto event = new EventDto();
        event.setTitle("title");
        event.setStartDate(LocalDateTime.now());
        event.setOwnerId(1L);
        Assertions.assertDoesNotThrow(() -> eventValidator.validateEventInfo(event));
    }
}