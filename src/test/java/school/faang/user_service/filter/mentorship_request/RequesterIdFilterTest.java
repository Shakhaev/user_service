package school.faang.user_service.filter.mentorship_request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RequesterIdFilterTest {
    private static final long MATCH_ID = 1L;
    private static final long NOT_MATCH_ID = 100000L;
    private RequesterIdFilter requesterIdFilter;
    private Stream<MentorshipRequest> mentorshipRequests;
    private MentorshipRequest request;

    @BeforeEach
    void setUp() {
        requesterIdFilter = new RequesterIdFilter();
        request = MentorshipRequest.builder().requester(User.builder().id(1L).build()).build();

        mentorshipRequests = Stream.of(request);
    }

    @Test
    void testIsApplicableReturnTrue() {
        assertTrue(requesterIdFilter.isApplicable(
                RequestFilterDto.builder().requesterId(MATCH_ID).build()));
    }

    @Test
    void testIsApplicableReturnFalse() {
        assertFalse(requesterIdFilter.isApplicable(RequestFilterDto.builder().build()));
    }

    @Test
    void testApplyWhenRequesterIdMatches() {
        List<MentorshipRequest> actualRequests = requesterIdFilter
                .apply(mentorshipRequests, RequestFilterDto.builder().requesterId(MATCH_ID).build()).toList();
        assertTrue(actualRequests.contains(request));
    }

    @Test
    void testApplyWhenRequesterIdNotMatches() {
        List<MentorshipRequest> actualRequests = requesterIdFilter
                .apply(mentorshipRequests, RequestFilterDto.builder().requesterId(NOT_MATCH_ID).build()).toList();
        assertTrue(actualRequests.isEmpty());
    }
}