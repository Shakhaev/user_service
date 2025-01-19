package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.service.SkillService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;
    @Spy
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillController skillController;

    private static final String SKILL_TITLE1 = "-title-1-";
    private static final String SKILL_TITLE2 = "-title-2-";
    private static final String SKILL_TITLE3 = "-title-3-";
    private static final Long SKILL_ID1 = 12345L;
    private static final Long SKILL_ID2 = 23456L;
    private static final Long SKILL_ID3 = 34567L;

    private Skill skill;
    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setTitle(SKILL_TITLE1);
        skill.setId(SKILL_ID1);

        skillDto = new SkillDto(SKILL_ID1, SKILL_TITLE1);
    }

    @Test
    void create() {
    }

    @Test
    void getUserSkills() {
    }

    @Test
    void getOfferedSkills() {
        final long userId = 2345L;
        Map<Skill, Long> sendedCandidatesMap = new HashMap<>();

        Skill candidate1 = skillMapper.toEntity(new SkillDto(SKILL_ID1, SKILL_TITLE1));
        Long offersAmount1 = 2L;
        Skill candidate2 = skillMapper.toEntity(new SkillDto(SKILL_ID2, SKILL_TITLE2));
        Long offersAmount2 = 3L;
        Skill candidate3 = skillMapper.toEntity(new SkillDto(SKILL_ID3, SKILL_TITLE3));
        Long offersAmount3 = 4L;

        sendedCandidatesMap.put(candidate1, offersAmount1);
        sendedCandidatesMap.put(candidate2, offersAmount2);
        sendedCandidatesMap.put(candidate3, offersAmount3);

        when(skillService.getOfferedSkills(userId)).thenReturn(sendedCandidatesMap);
        List<SkillCandidateDto> returnedCandidatesDto = skillController.getOfferedSkills(userId);

        assertEquals(sendedCandidatesMap.size(), returnedCandidatesDto.size());
        for (SkillCandidateDto candidateDto : returnedCandidatesDto) {
            sendedCandidatesMap.entrySet().stream().filter(entry ->
                            entry.getKey().getTitle().equals(candidateDto.getSkill().getTitle()))
                    .forEach(entry ->
                            assertEquals(entry.getValue(), candidateDto.getOffersAmount()));
        }
    }

    @Test
    void acquireSkillFromOffers() {
        final long skillId = 123L;
        final long userId = 2345L;
        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(skill);

        SkillDto testDto = skillController.acquireSkillFromOffers(skillId, userId);
        assertEquals(skillDto, testDto);
    }
}