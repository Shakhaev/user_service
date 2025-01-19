package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.filter.RequestFilterDto;
import school.faang.user_service.service.filter.StatusFilter;

import java.util.List;
import java.util.stream.Stream;

public class StatusFilterTest {

    @Test
    public void testIsApplicableIfStatusPatternIsAbsentThenReturnFalse() {
        RequestFilterDto filters = RequestFilterDto.builder()
                .authorPattern("author")
                .build();
        StatusFilter filter = new StatusFilter();

        Assertions.assertFalse(filter.isApplicable(filters));
    }

    @Test
    public void testIsApplicableIfStatusPatternIsPresentThenReturnTrue() {
        RequestFilterDto filters = RequestFilterDto.builder()
                .statusPattern("PENDING")
                .build();
        StatusFilter filter = new StatusFilter();

        Assertions.assertTrue(filter.isApplicable(filters));
    }

    @Test
    public void testApplySuccess() {
        StatusFilter filter = new StatusFilter();
        Stream<MentorshipRequest> mentorshipRequestStream = TestData.getMentorshipRequestsStream();
        RequestFilterDto filtersDto = RequestFilterDto.builder()
                .statusPattern("PENDING")
                .build();

        List<MentorshipRequest> result = filter.apply(mentorshipRequestStream, filtersDto)
                .toList();

        Assertions.assertEquals(1, result.size());
    }
}
