package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.filter.ReceiverFilter;
import school.faang.user_service.service.filter.RequestFilterDto;

import java.util.List;
import java.util.stream.Stream;

public class ReceiverFilterTest {

    @Test
    public void testIsApplicableIfReceiverPatternIsAbsentThenReturnFalse() {
        RequestFilterDto filters = RequestFilterDto.builder()
                .authorPattern("author")
                .build();
        ReceiverFilter filter = new ReceiverFilter();

        Assertions.assertFalse(filter.isApplicable(filters));
    }

    @Test
    public void testIsApplicableIfReceiverPatternIsPresentThenReturnTrue() {
        RequestFilterDto filters = RequestFilterDto.builder()
                .receiverPattern("Jack")
                .build();
        ReceiverFilter filter = new ReceiverFilter();

        Assertions.assertTrue(filter.isApplicable(filters));
    }

    @Test
    public void testApplySuccess() {
        ReceiverFilter filter = new ReceiverFilter();
        Stream<MentorshipRequest> mentorshipRequestStream = TestData.getMentorshipRequestsStream();
        RequestFilterDto filtersDto = RequestFilterDto.builder()
                .receiverPattern("Jack")
                .build();

        List<MentorshipRequest> result = filter.apply(mentorshipRequestStream, filtersDto)
                .toList();

        Assertions.assertEquals(3, result.size());
    }
}
