package school.faang.user_service.service;


import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Captor
    private ArgumentCaptor<Skill> captor;

    @Captor
    private ArgumentCaptor<Long> captorLong;

    @InjectMocks
    private SkillService skillService;

    SkillDto skillDto;

    @BeforeEach
    public void init() {
        skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("John");
    }

    @Test
    public void testSkillIsSaved() {
        Mockito.when(skillRepository.save(any(Skill.class))).thenReturn(new Skill());
        skillService.create(skillDto);
        verify(skillRepository, times(1)).save(captor.capture());
        Skill skill = captor.getValue();
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    @DisplayName("Testing return DataValidationException")
    public void testSkillIsNotSaved() {
        Mockito.when(skillRepository.save(any(Skill.class))).thenThrow(new DataValidationException("DataValidationException!!!"));

        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    @DisplayName("get skills from Repo")
    public void testSkillsFromRepo() {
        Mockito.when(skillRepository.findAllByUserId(any(Long.class))).thenReturn(new ArrayList<>());
        List<SkillDto> skillDtoList = skillService.getUserSkills(any(Long.class));
        /*verify(skillRepository, times(1)).findAllByUserId((Long) captorLong.capture());
        SkillDto skill = captorLong.getValue();
        assertEquals(skillDtoList, skillService.getUserSkills(skill).);*/


    }
}
