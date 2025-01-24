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

public class RecommendationRequestReceiverFilterTest extends RecommendationRequestFilterTest {
    @BeforeEach
    void setUp() {
        filter = new RecommendationRequestReceiverFilter();
        filters = new RecommendationRequestFilterDto();
    }

    @Test
    void testIsApplicable_ShouldReturnTrueWhenReceiverPatternIsNotNull() {
        filters.setReceiverPattern("receiver");
        boolean isApplicable = filter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    void testIssApplicable_ShouldReturnFalseWhenReceiverPatternIsNull() {
        boolean isApplicable = filter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    void testApply_ShouldFilterRequestsWithMatchingReceiverPattern() {
        filters.setReceiverPattern("receiver");

        request1 = new RecommendationRequest();
        request1.setReceiver(User.builder().username("receiver1").build());

        request2 = new RecommendationRequest();
        request2.setReceiver(User.builder().username("receiver2").build());

        request3 = new RecommendationRequest();
        request3.setReceiver(User.builder().username("user").build());

        Stream<RecommendationRequest> requests = Stream.of(request1, request2, request3);
        List<RecommendationRequest> expected = List.of(request1, request2);
        List<RecommendationRequest> filteredRequests = filter.apply(requests, filters).toList();

        assertEquals(expected, filteredRequests);
    }

    @Test
    void testApply_ShouldReturnEmptyStreamWhenNoRequestsMatch() {
        filters.setReceiverPattern("user");

        request1 = new RecommendationRequest();
        request1.setReceiver(User.builder().username("receiver1").build());

        request2 = new RecommendationRequest();
        request2.setReceiver(User.builder().username("receiver2").build());

        Stream<RecommendationRequest> requests = Stream.of(request1, request2);
        List<RecommendationRequest> filteredRequests = filter.apply(requests, filters).toList();

        assertTrue(filteredRequests.isEmpty());
    }
}
