package school.faang.user_service.filter.recommendation_request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendationRequestRequesterFilterTest extends RecommendationRequestFilterTest {
    @BeforeEach
    void setUp() {
        filter = new RecommendationRequestRequesterFilter();
        filters = new RecommendationRequestFilterDto();
    }

    @Test
    void testIsApplicable_ShouldReturnTrueWhenRequesterPatternIsNotNull() {
        filters.setRequesterPattern("user");
        boolean isApplicable = filter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    void testIssApplicable_ShouldReturnFalseWhenRequesterPatternIsNull() {
        boolean isApplicable = filter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    void testApply_ShouldFilterRequestsWithMatchingRequesterPattern() {
        filters.setRequesterPattern("requester");

        request1 = new RecommendationRequest();
        request1.setRequester(User.builder().username("requester1").build());

        request2 = new RecommendationRequest();
        request2.setRequester(User.builder().username("user").build());

        request3 = new RecommendationRequest();
        request3.setRequester(User.builder().username("requester2").build());

        Stream<RecommendationRequest> requests = Stream.of(request1, request2, request3);
        List<RecommendationRequest> expected = List.of(request1, request3);
        List<RecommendationRequest> filteredRequests = filter.apply(requests, filters).toList();

        assertEquals(expected, filteredRequests);
    }

    @Test
    void testApply_ShouldReturnEmptyStreamWhenNoRequestsMatch() {
        filters.setRequesterPattern("user");

        request1 = new RecommendationRequest();
        request1.setRequester(User.builder().username("requester1").build());

        request2 = new RecommendationRequest();
        request2.setRequester(User.builder().username("requester2").build());

        Stream<RecommendationRequest> requests = Stream.of(request1, request2);
        List<RecommendationRequest> filteredRequests = filter.apply(requests, filters).toList();

        assertTrue(filteredRequests.isEmpty());
    }
}
