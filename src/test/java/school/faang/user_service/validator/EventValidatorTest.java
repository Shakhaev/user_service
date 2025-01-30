package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.service.SkillService;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Spy
    private EventMapperImpl eventMapper;
    @InjectMocks
    private EventValidator eventValidator;
    @Mock
    private SkillService skillService;

    @Test
    void validateCreatorSkills_ShouldThrowWithoutNecessarySkills() {
        SkillDto skill = new SkillDto();
        skill.setId(1L);
        Mockito.when(skillService.findSkillsByUserId(Mockito.anyLong()))
                .thenReturn(List.of(skill));

        CreateEventDto createEventDto = new CreateEventDto();
        createEventDto.setOwnerId(1L);
        createEventDto.setRelatedSkillsIds(List.of(2L, 3L));

        Assertions.assertThrows(DataValidationException.class, () ->
                eventValidator.validateCreatorSkills(createEventDto));
    }

    @Test
    void validateCreatorSkills_ShouldNotThrowWithCorrectCreatorSkills() {
        SkillDto skill = new SkillDto();
        skill.setId(1L);
        SkillDto skill1 = new SkillDto();
        skill1.setId(2L);
        SkillDto skill2 = new SkillDto();
        skill2.setId(3L);
        Mockito.when(skillService.findSkillsByUserId(Mockito.anyLong()))
                .thenReturn(List.of(skill, skill2, skill1));

        CreateEventDto createEventDto = new CreateEventDto();
        createEventDto.setOwnerId(1L);
        createEventDto.setRelatedSkillsIds(List.of(1L, 2L));

        Assertions.assertDoesNotThrow(() -> eventValidator.validateCreatorSkills(createEventDto));
    }

    @Test
    void validateEventInfo_ShouldThrowWithBlankTitle() {
        CreateEventDto event = new CreateEventDto();
        event.setTitle(" \n\t ");
        Assertions.assertThrows(DataValidationException.class,
                () -> eventValidator.validateEventInfo(eventMapper.fromCreateDtoToEntity(event)));
    }

    @Test
    void validateEventInfo_ShouldThrowWithNullTitle() {
        CreateEventDto event = new CreateEventDto();
        Assertions.assertThrows(DataValidationException.class,
                () -> eventValidator.validateEventInfo(eventMapper.fromCreateDtoToEntity(event)));
    }

    @Test
    void validateEventInfo_ShouldThrowWithNullDate() {
        CreateEventDto event = new CreateEventDto();
        event.setTitle("title");
        Assertions.assertThrows(DataValidationException.class,
                () -> eventValidator.validateEventInfo(eventMapper.fromCreateDtoToEntity(event)));
    }
    
    @Test
    void validateEventInfo_ShouldNotThrowWithCorrectEvent() {
        CreateEventDto event = new CreateEventDto();
        event.setTitle("title");
        event.setStartDate(LocalDateTime.now());
        Assertions.assertDoesNotThrow(() -> eventValidator.validateEventInfo(eventMapper.fromCreateDtoToEntity(event)));
    }
}