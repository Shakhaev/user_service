package school.faang.user_service.service.mentorship.filters;

import org.junit.Test;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DescriptionRequestFilterTest {

    private DescriptionRequestFilter descriptionFilter;
    private final RequestFilterDto filterDto = new RequestFilterDto();

    private final Stream<MentorshipRequest> requests = Stream.of(
            MentorshipRequest.builder().description("lear coding").build(),
            MentorshipRequest.builder().description("Java Developer").build(),
            MentorshipRequest.builder().description("Python or *Java").build(),
            MentorshipRequest.builder().description(" ").build());


    @Test
    public void testIsNotApplicable() {
        assertFalse(descriptionFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable() {
        filterDto.setDescription("apply");
        assertTrue(descriptionFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        filterDto.setDescription("Java");

        List<MentorshipRequest> result = descriptionFilter.apply(requests, filterDto).toList();

        assertEquals(2, result.size());
        result.forEach(request -> assertTrue(request.getDescription().contains("Java")));
    }
}
