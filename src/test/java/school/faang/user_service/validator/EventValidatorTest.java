package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Spy
    private EventMapperImpl eventMapper;
    @InjectMocks
    private EventValidator eventValidator;
    @Mock
    private UserService userService;

    @Test
    void shouldThrowOnCreatorWithoutNecessarySkills() {
        UserDto userDto = new UserDto();
        userDto.setSkillsIds(List.of());
        Mockito.when(userService.findUserById(Mockito.anyLong())).thenReturn(userDto);

        EventCreateDto eventCreateDto = new EventCreateDto();
        eventCreateDto.setOwnerId(1L);
        eventCreateDto.setRelatedSkillsIds(List.of(2L, 3L));

        Assertions.assertThrows(DataValidationException.class, () -> eventValidator.validateEventCreatorSkills(eventCreateDto));
    }

    @Test
    void shouldNotThrowWithCorrectCreatorSkills() {
        UserDto userDto = new UserDto();
        userDto.setSkillsIds(List.of(1L, 2L, 3L));
        Mockito.when(userService.findUserById(Mockito.anyLong())).thenReturn(userDto);

        EventCreateDto EventCreateDto = new EventCreateDto();
        EventCreateDto.setOwnerId(1L);
        EventCreateDto.setRelatedSkillsIds(List.of(1L, 2L));

        Assertions.assertDoesNotThrow(() -> eventValidator.validateEventCreatorSkills(EventCreateDto));
    }

    @Test
    void shouldThrowWithBlankTitle() {
        EventCreateDto event = new EventCreateDto();
        event.setTitle(" \n\t ");
        Assertions.assertThrows(DataValidationException.class,
                () -> eventValidator.validateEventInfo(eventMapper.fromCreateDtoToEntity(event)));
    }

    @Test
    void shouldThrowWithNullTitle() {
        EventCreateDto event = new EventCreateDto();
        Assertions.assertThrows(DataValidationException.class,
                () -> eventValidator.validateEventInfo(eventMapper.fromCreateDtoToEntity(event)));
    }

    @Test
    void shouldThrowWithNullDate() {
        EventCreateDto event = new EventCreateDto();
        event.setTitle("title");
        Assertions.assertThrows(DataValidationException.class,
                () -> eventValidator.validateEventInfo(eventMapper.fromCreateDtoToEntity(event)));
    }
    
    @Test
    void shouldNotThrowWithCorrectEvent() {
        EventCreateDto event = new EventCreateDto();
        event.setTitle("title");
        event.setStartDate(LocalDateTime.now());
        Assertions.assertDoesNotThrow(() -> eventValidator.validateEventInfo(eventMapper.fromCreateDtoToEntity(event)));
    }
}