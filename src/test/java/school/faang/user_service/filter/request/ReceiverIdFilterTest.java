package school.faang.user_service.filter.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ReceiverIdFilterTest {
    private static final long MATCH_ID = 1L;
    private static final long NOT_MATCH_ID = 100000L;
    private ReceiverIdFilter receiverIdFilter;
    private Stream<RecommendationRequest> recommendationRequest;
    private RecommendationRequest request;


    @BeforeEach
    void setUp() {
        receiverIdFilter = new ReceiverIdFilter();
        request = RecommendationRequest.builder().receiver(User.builder().id(1L).build()).build();

        recommendationRequest = Stream.of(request);
    }

    @Test
    void testIsApplicableReturnTrue() {
        assertTrue(receiverIdFilter.isApplicable(
                RequestFilterDto.builder().receiverId(MATCH_ID).build()));
    }

    @Test
    void testIsApplicableReturnFalse() {
        assertFalse(receiverIdFilter.isApplicable(RequestFilterDto.builder().build()));
    }

    @Test
    void testApplyWhenReceiverIdMatches() {
        List<RecommendationRequest> actualRequests = receiverIdFilter
                .apply(recommendationRequest, RequestFilterDto.builder().receiverId(MATCH_ID).build()).toList();
        assertTrue(actualRequests.contains(request));
    }

    @Test
    void testApplyWhenReceiverIdNotMatches() {
        List<RecommendationRequest> actualRequests = receiverIdFilter
                .apply(recommendationRequest, RequestFilterDto.builder().receiverId(NOT_MATCH_ID).build()).toList();
        assertTrue(actualRequests.isEmpty());
    }
}