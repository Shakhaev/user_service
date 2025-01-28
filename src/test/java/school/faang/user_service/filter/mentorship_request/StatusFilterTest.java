package school.faang.user_service.filter.mentorship_request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StatusFilterTest {
    private static final RequestStatus MATCH_STATUS = RequestStatus.ACCEPTED;
    private static final RequestStatus NOT_MATCH_STATUS = RequestStatus.PENDING;
    private StatusFilter statusFilter;
    private Stream<MentorshipRequest> mentorshipRequests;
    private MentorshipRequest request;

    @BeforeEach
    void setUp() {
        statusFilter = new StatusFilter();
        request = MentorshipRequest.builder().status(MATCH_STATUS).build();

        mentorshipRequests = Stream.of(request);
    }

    @Test
    void testIsApplicableReturnTrue() {
        assertTrue(statusFilter.isApplicable(
                RequestFilterDto.builder().status(MATCH_STATUS).build()));
    }

    @Test
    void testIsApplicableReturnFalse() {
        assertFalse(statusFilter.isApplicable(RequestFilterDto.builder().build()));
    }

    @Test
    void testApplyWhenStatusMatches() {
        List<MentorshipRequest> actualRequests = statusFilter
                .apply(mentorshipRequests, RequestFilterDto.builder().status(MATCH_STATUS).build()).toList();
        assertTrue(actualRequests.contains(request));
    }

    @Test
    void testApplyWhenStatusNotMatches() {
        List<MentorshipRequest> actualRequests = statusFilter
                .apply(mentorshipRequests, RequestFilterDto.builder().status(NOT_MATCH_STATUS).build()).toList();
        assertTrue(actualRequests.isEmpty());
    }
}