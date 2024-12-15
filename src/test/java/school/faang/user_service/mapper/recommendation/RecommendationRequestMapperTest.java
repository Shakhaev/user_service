package school.faang.user_service.mapper.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationRequestMapperTest {
    private final RecommendationRequestMapperImpl mapper = new RecommendationRequestMapperImpl();

    private RecommendationRequest entity;
    private RecommendationRequest entityForToEntityExpected;
    private RecommendationRequestDto dto;

    @BeforeEach
    void setUp() {
        User requester = User.builder()
                .id(201L).build();

        User receiver = User.builder()
                .id(202L)
                .build();

        Skill skill1 = Skill.builder()
                .id(101L)
                .build();

        Skill skill2 = Skill.builder()
                .id(102L)
                .build();

        Skill skill3 = Skill.builder()
                .id(103L)
                .build();

        entity = RecommendationRequest.builder()
                .id(1L)
                .message("Test")
                .status(RequestStatus.PENDING)
                .requester(requester)
                .receiver(receiver)
                .skills(null)
                .createdAt(LocalDateTime.of(2024, 4, 27, 10, 15, 30))
                .updatedAt(LocalDateTime.of(2024, 4, 27, 10, 15, 30))
                .rejectionReason(null)
                .recommendation(null)
                .build();

        SkillRequest skillRequest1 = new SkillRequest();
        skillRequest1.setSkill(skill1);

        SkillRequest skillRequest2 = new SkillRequest();
        skillRequest2.setSkill(skill2);

        SkillRequest skillRequest3 = new SkillRequest();
        skillRequest3.setSkill(skill3);

        List<SkillRequest> skills = Arrays.asList(skillRequest1, skillRequest2, skillRequest3);
        entity.setSkills(skills);

        entityForToEntityExpected = RecommendationRequest.builder()
                .id(1L)
                .message("Test")
                .status(RequestStatus.PENDING)
                .requester(null)
                .receiver(null)
                .skills(null)
                .createdAt(LocalDateTime.of(2024, 4, 27, 10, 15, 30))
                .updatedAt(LocalDateTime.of(2024, 4, 27, 10, 15, 30))
                .rejectionReason(null)
                .recommendation(null)
                .build();

        dto = new RecommendationRequestDto(
                1L,
                "Test",
                RequestStatus.PENDING,
                Arrays.asList(101L, 102L, 103L),
                201L,
                202L,
                LocalDateTime.of(2024, 4, 27, 10, 15, 30),
                LocalDateTime.of(2024, 4, 27, 10, 15, 30)
        );
    }

    @Test
    void testToDTO() {
        RecommendationRequestDto dtoForMapper = mapper.toDTO(entity);
        assertEquals(dto, dtoForMapper);

    }

    @Test
    void testToEntity() {
        RecommendationRequest entityForToEntity = mapper.toEntity(dto);
        assertEquals(entityForToEntityExpected, entityForToEntity);
    }
}