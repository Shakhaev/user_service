package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.filter.DescriptionFilter;
import school.faang.user_service.service.filter.RequestFilterDto;

import java.util.List;
import java.util.stream.Stream;

public class DescriptionFilterTest {

    @Test
    public void testIsApplicableIfDescriptionPatternIsAbsentThenReturnFalse() {
        RequestFilterDto filters = RequestFilterDto.builder()
                .authorPattern("author")
                .build();
        DescriptionFilter filter = new DescriptionFilter();

        Assertions.assertFalse(filter.isApplicable(filters));
    }

    @Test
    public void testIsApplicableIfDescriptionPatternIsPresentThenReturnTrue() {
        RequestFilterDto filters = RequestFilterDto.builder()
                .descriptionPattern("description")
                .build();
        DescriptionFilter filter = new DescriptionFilter();

        Assertions.assertTrue(filter.isApplicable(filters));
    }

    @Test
    public void testApplySuccess() {
        DescriptionFilter filter = new DescriptionFilter();
        Stream<MentorshipRequest> mentorshipRequestStream = TestData.getMentorshipRequestsStream();
        RequestFilterDto filtersDto = RequestFilterDto.builder()
                .descriptionPattern("descri")
                .build();

        List<MentorshipRequest> result = filter.apply(mentorshipRequestStream, filtersDto)
                .toList();

        Assertions.assertEquals(4, result.size());
    }
}
