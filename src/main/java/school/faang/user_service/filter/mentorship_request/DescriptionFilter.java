package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.MentorshipRequestFilter;

import java.util.stream.Stream;

@Component
public class DescriptionFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        String description = filter.getDescriptionPattern();
        return description != null && !description.isBlank();
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> elements, RequestFilterDto filter) {
        return elements.filter(request -> request.getDescription().contains(filter.getDescriptionPattern()));
    }
}
