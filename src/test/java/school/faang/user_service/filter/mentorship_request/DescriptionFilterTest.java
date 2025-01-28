package school.faang.user_service.filter.mentorship_request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DescriptionFilterTest {

    private DescriptionFilter descriptionFilter;
    private Stream<MentorshipRequest> mentorshipRequests;
    private MentorshipRequest matchRequest1;
    private MentorshipRequest matchRequest2;
    private MentorshipRequest nonMatchRequest1;
    private MentorshipRequest nonMatchRequest2;

    @BeforeEach
    void setUp() {
        descriptionFilter = new DescriptionFilter();
        matchRequest1 = MentorshipRequest.builder().description("описание").build();
        matchRequest2 = MentorshipRequest.builder().description("большое описание").build();
        nonMatchRequest1 = MentorshipRequest.builder().description("неподходящий").build();
        nonMatchRequest2 = MentorshipRequest.builder().description("").build();

        mentorshipRequests = Stream.of(matchRequest1, matchRequest2, nonMatchRequest1, nonMatchRequest2);
    }

    @Test
    void testIsApplicableReturnTrue() {
        assertTrue(descriptionFilter.isApplicable(
                RequestFilterDto.builder().descriptionPattern("описание").build()));
    }

    @ParameterizedTest
    @MethodSource("provideRequestFilterDtos")
    void testIsApplicableReturnFalse(RequestFilterDto dto) {
        assertFalse(descriptionFilter.isApplicable(dto));
    }

    private static Stream<RequestFilterDto> provideRequestFilterDtos() {
        return Stream.of(
                RequestFilterDto.builder().descriptionPattern("      ").build(),
                RequestFilterDto.builder().descriptionPattern("").build(),
                RequestFilterDto.builder().build()
        );
    }

    @Test
    void testApplyWhenDescriptionMatches() {
        List<MentorshipRequest> actualRequests = descriptionFilter
                .apply(mentorshipRequests, RequestFilterDto.builder().descriptionPattern("описание").build()).toList();
        assertTrue(actualRequests.contains(matchRequest1));
        assertTrue(actualRequests.contains(matchRequest2));
        assertFalse(actualRequests.contains(nonMatchRequest1));
        assertFalse(actualRequests.contains(nonMatchRequest2));
    }

    @Test
    void testApplyWhenDescriptionNotMatches() {
        List<MentorshipRequest> actualRequests = descriptionFilter
                .apply(mentorshipRequests, RequestFilterDto.builder().descriptionPattern("wrong").build()).toList();
        assertTrue(actualRequests.isEmpty());
    }
}