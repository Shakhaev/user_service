package school.faang.user_service.filter.recommendation_request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendationRequestSkillFilterTest extends RecommendationRequestFilterTest {
    @BeforeEach
    void setUp() {
        filter = new RecommendationRequestSkillFilter();
        filters = new RecommendationRequestFilterDto();
    }

    @Test
    void testIsApplicable_ShouldReturnTrue_WhenSkillPatternIsNotNull() {
        filters.setSkillPattern("Spring");
        boolean isApplicable = filter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    void testIssApplicable_ShouldReturnFalse_WhenSkillPatternIsNull() {
        boolean isApplicable = filter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    void testApply_ShouldFilterRequestsWithMatchingSkillPattern() {
        filters.setSkillPattern("Redis");

        SkillRequest skill1 = new SkillRequest();
        skill1.setSkill(Skill.builder().title("Redis").build());

        SkillRequest skill2 = new SkillRequest();
        skill2.setSkill(Skill.builder().title("C#").build());

        request1 = new RecommendationRequest();
        request1.setSkills(List.of(skill1, skill2));

        request2 = new RecommendationRequest();
        request2.setSkills(List.of(skill2));

        request3 = new RecommendationRequest();
        request3.setSkills(List.of(skill1));

        Stream<RecommendationRequest> requests = Stream.of(request1, request2, request3);
        List<RecommendationRequest> expected = List.of(request1, request3);
        List<RecommendationRequest> filteredRequests = filter.apply(requests, filters).toList();

        assertEquals(expected, filteredRequests);
    }

    @Test
    void testApply_ShouldReturnEmptyStream_WhenNoRequestsMatch() {
        filters.setSkillPattern("C#");

        SkillRequest skill1 = new SkillRequest();
        skill1.setSkill(Skill.builder().title("Redis").build());

        SkillRequest skill2 = new SkillRequest();
        skill2.setSkill(Skill.builder().title("Java").build());

        request1 = new RecommendationRequest();
        request1.setSkills(List.of(skill1, skill2));

        request2 = new RecommendationRequest();
        request2.setSkills(List.of(skill2));

        Stream<RecommendationRequest> requests = Stream.of(request1, request2);
        List<RecommendationRequest> filteredRequests = filter.apply(requests, filters).toList();

        assertTrue(filteredRequests.isEmpty());
    }
}
