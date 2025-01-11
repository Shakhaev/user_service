package school.faang.user_service.service;


import org.junit.Assert;
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
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @Captor
    private ArgumentCaptor<Skill> captor;

    @Spy
    private SkillController skillController = Mappers.getMapper(SkillController.class);

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @InjectMocks
    private SkillService skillService;

    @Test
    public void testSkillIsSaved() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("Java");
        Mockito.when(skillRepository.save(any(Skill.class))).thenReturn(new Skill());
        skillService.create(skillDto);
        verify(skillRepository, times(1)).save(captor.capture());
        Skill skill = captor.getValue();
        Assert.assertEquals(skillDto.getTitle(), skill.getTitle());
    }
}
