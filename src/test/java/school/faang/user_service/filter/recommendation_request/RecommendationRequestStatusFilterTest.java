package school.faang.user_service.filter.recommendation_request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendationRequestStatusFilterTest extends RecommendationRequestFilterTest {
    @BeforeEach
    void setUp() {
        filter = new RecommendationRequestStatusFilter();
        filters = new RecommendationRequestFilterDto();
    }

    @Test
    void testIsApplicable_ShouldReturnTrueWhenStatusIsNotNull() {
        filters.setStatus(RequestStatus.ACCEPTED);
        boolean isApplicable = filter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    void testIssApplicable_ShouldReturnFalseWhenStatusIsNull() {
        boolean isApplicable = filter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    void testApply_ShouldFilterRequestsWithMatchingStatusFilter() {
        filters.setStatus(RequestStatus.ACCEPTED);

        request1 = new RecommendationRequest();
        request1.setStatus(RequestStatus.ACCEPTED);

        request2 = new RecommendationRequest();
        request2.setStatus(RequestStatus.PENDING);

        request3 = new RecommendationRequest();
        request3.setStatus(RequestStatus.REJECTED);

        Stream<RecommendationRequest> requests = Stream.of(request1, request2, request3);
        List<RecommendationRequest> expected = List.of(request1);
        List<RecommendationRequest> filteredRequests = filter.apply(requests, filters).toList();

        assertEquals(expected, filteredRequests);
    }

    @Test
    void testApply_ShouldReturnEmptyStreamWhenNoRequestsMatch() {
        filters.setStatus(RequestStatus.ACCEPTED);

        request1 = new RecommendationRequest();
        request1.setStatus(RequestStatus.REJECTED);

        request2 = new RecommendationRequest();
        request2.setStatus(RequestStatus.PENDING);

        Stream<RecommendationRequest> requests = Stream.of(request1, request2);
        List<RecommendationRequest> filteredRequests = filter.apply(requests, filters).toList();

        assertTrue(filteredRequests.isEmpty());
    }
}
